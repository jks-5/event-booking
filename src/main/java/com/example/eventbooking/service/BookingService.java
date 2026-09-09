package com.example.eventbooking.service;

import com.example.eventbooking.dto.BookingResponse;
import com.example.eventbooking.dto.EventResponse;
import com.example.eventbooking.dto.MyBookingsFilter;
import com.example.eventbooking.dto.MyBookingsResponse;
import com.example.eventbooking.exception.*;
import com.example.eventbooking.model.Booking;
import com.example.eventbooking.model.Event;
import com.example.eventbooking.model.User;
import com.example.eventbooking.repository.BookingRepository;
import com.example.eventbooking.repository.EventRepository;
import com.example.eventbooking.repository.UserRepository;
import com.example.eventbooking.repository.specification.BookingSpecifications;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;

@Service
public class BookingService {

    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EntityManager entityManager;

    BookingService(BookingRepository repository, UserRepository userRepository, EventRepository eventRepository, EntityManager entityManager) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
            backoff = @Backoff(delay = 100)
    )
    public BookingResponse bookEvent(Long eventId, Long userId) {
        Event event = eventRepository.findWithOptimisticLockById(eventId).orElseThrow(EventNotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        validateRegistrationWindow(event);
        Booking.Status status = checkBookingStatus(event);

        Booking booking = new Booking(user, event, status);

        repository.save(booking);
        entityManager.flush();

        return new BookingResponse(booking);
    }

    @Transactional
    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
            backoff = @Backoff(delay = 100)
    )
    public BookingResponse cancelBooking(Long eventId, Long userId) {

        // optimistic locking on the event
        eventRepository.findWithOptimisticLockById(eventId).orElseThrow(EventNotFoundException::new);

        Booking booking = repository.findByEventIdAndUserId(eventId, userId).orElseThrow(BookingNotFoundException::new);

        Booking.Status previousStatus = booking.getStatus();

        if (previousStatus == Booking.Status.CANCELLED) {
            throw new BookingAlreadyCancelledException();
        }

        booking.setStatus(Booking.Status.CANCELLED);

        if(previousStatus == Booking.Status.CONFIRMED) {
            repository.findFirstByEventIdAndStatusOrderByRegisteredAtAsc(eventId, Booking.Status.WAITLISTED)
                    .ifPresent(waitlistedBooking -> waitlistedBooking.setStatus(Booking.Status.CONFIRMED));
        }

        return new BookingResponse(repository.save(booking));
    }

    public Page<MyBookingsResponse> getMyBookings(Long userId, Pageable pageable, MyBookingsFilter filter) {
        Specification<Booking> specification = Specification
                .where(BookingSpecifications.hasUserId(userId))
                .and(BookingSpecifications.hasEventTitle(filter.getTitle()))
                .and(BookingSpecifications.hasEventLocation(filter.getLocation()))
                .and(BookingSpecifications.hasStatus(filter.getStatus()));

        return repository.findAll(specification, pageable).map(booking -> new MyBookingsResponse(booking, new EventResponse(booking.getEvent())));
    }

    private Booking.Status checkBookingStatus(Event event) {
        int numberOfParticipants = repository.countByEventIdAndStatus(event.getId(), Booking.Status.CONFIRMED);
        if (numberOfParticipants < event.getMaxParticipants()) {
            return Booking.Status.CONFIRMED;
        }

        int waitlistedParticipants = repository.countByEventIdAndStatus(event.getId(), Booking.Status.WAITLISTED);
        if (waitlistedParticipants >= event.getWaitlistSpots()) {
            throw new MaxCapacityReachedException();
        }

        return Booking.Status.WAITLISTED;
    }

    private void validateRegistrationWindow(Event event) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime regStart = event.getRegistrationStart();
        OffsetDateTime regEnd = event.getRegistrationEnd();

        if (now.isBefore(regStart) || now.isAfter(regEnd)) {
            throw new RegistrationWindowClosedException("Registration is only allowed between " + regStart + " and " + regEnd);
        }
    }
}
