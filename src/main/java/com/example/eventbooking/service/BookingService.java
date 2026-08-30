package com.example.eventbooking.service;

import com.example.eventbooking.dto.BookingResponse;
import com.example.eventbooking.exception.*;
import com.example.eventbooking.model.Booking;
import com.example.eventbooking.model.Event;
import com.example.eventbooking.model.User;
import com.example.eventbooking.repository.BookingRepository;
import com.example.eventbooking.repository.EventRepository;
import com.example.eventbooking.repository.UserRepository;
import jakarta.persistence.EntityManager;
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
        Event event = eventRepository.findWithVersionIncrementById(eventId).orElseThrow(EventNotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        checkEventCapacity(event);
        validateRegistrationWindow(event);

        Booking booking = new Booking(user, event);

        repository.save(booking);
        entityManager.flush();

        return new BookingResponse(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long eventId, Long userId) {
        Booking booking = repository.findByEventIdAndUserId(eventId, userId).orElseThrow(BookingNotFoundException::new);

        if (booking.getStatus() == Booking.Status.CANCELLED) {
            throw new BookingAlreadyCancelledException();
        }

        booking.setStatus(Booking.Status.CANCELLED);

        return new BookingResponse(repository.save(booking));
    }

    private void checkEventCapacity(Event event) {
        int numberOfParticipants = repository.countByEventIdAndStatus(event.getId(), Booking.Status.CONFIRMED);
        if (numberOfParticipants >= event.getMaxParticipants()) {
            throw new MaxCapacityReachedException();
        }
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
