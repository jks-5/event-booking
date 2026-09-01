package com.example.eventbooking.service;

import com.example.eventbooking.dto.CreateEventRequest;
import com.example.eventbooking.dto.EventResponse;
import com.example.eventbooking.dto.UpdateEventRequest;
import com.example.eventbooking.dto.UserResponse;
import com.example.eventbooking.exception.EventNotFoundException;
import com.example.eventbooking.exception.InvalidEventScheduleException;
import com.example.eventbooking.exception.UserNotFoundException;
import com.example.eventbooking.model.Booking;
import com.example.eventbooking.model.CustomUserDetails;
import com.example.eventbooking.model.Event;
import com.example.eventbooking.model.User;
import com.example.eventbooking.repository.BookingRepository;
import com.example.eventbooking.repository.EventRepository;
import com.example.eventbooking.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    EventService(EventRepository repository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<EventResponse> getAllEvents() {
        return repository.findAll().stream().map(EventResponse::new).toList();
    }
    public EventResponse createEvent(CreateEventRequest request, Long id) {

        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        validateSchedule(
                request.getRegistrationStart(),
                request.getRegistrationEnd(),
                request.getStartTime(),
                request.getEndTime()
        );

        Event event = new Event(
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getMaxParticipants(),
                request.getWaitlistSpots(),
                request.getStartTime(),
                request.getEndTime(),
                request.getRegistrationStart(),
                request.getRegistrationEnd(),
                user
        );

        return new EventResponse(repository.save(event));
    }

    public EventResponse getEventById(Long id) {
        Event event = repository.findById(id).orElseThrow(EventNotFoundException::new);

        return new EventResponse(event);
    }

    @Transactional
    public EventResponse updateEvent(Long id, UpdateEventRequest request) {
        Event event = validateOwner(repository.findById(id).orElseThrow(EventNotFoundException::new));

        String title = getNewOrCurrentValue(request.getTitle(), event.getTitle());
        String description = getNewOrCurrentValue(request.getDescription(), event.getDescription());
        String location = getNewOrCurrentValue(request.getLocation(), event.getLocation());
        Integer maxParticipants = getNewOrCurrentValue(request.getMaxParticipants(), event.getMaxParticipants());
        Integer waitlistSpots = getNewOrCurrentValue(request.getWaitlistSpots(), event.getWaitlistSpots());
        OffsetDateTime startTime = getNewOrCurrentValue(request.getStartTime(), event.getStartTime());
        OffsetDateTime endTime = getNewOrCurrentValue(request.getEndTime(), event.getEndTime());
        OffsetDateTime registrationStart = getNewOrCurrentValue(request.getRegistrationStart(), event.getRegistrationStart());
        OffsetDateTime registrationEnd = getNewOrCurrentValue(request.getRegistrationEnd(), event.getRegistrationEnd());

        validateSchedule(
                registrationStart,
                registrationEnd,
                startTime,
                endTime
        );

        event.setTitle(title);
        event.setDescription(description);
        event.setLocation(location);
        event.setMaxParticipants(maxParticipants);
        event.setWaitlistSpots(waitlistSpots);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setRegistrationStart(registrationStart);
        event.setRegistrationEnd(registrationEnd);

        return new EventResponse(repository.save(event));
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event event = validateOwner(repository.findById(id).orElseThrow(EventNotFoundException::new));

        repository.delete(event);
    }

    public List<UserResponse> getParticipants(Long id) {
        validateOwner(repository.findById(id).orElseThrow(EventNotFoundException::new));

        return bookingRepository.findByEventIdAndStatus(id, Booking.Status.CONFIRMED).stream().map(booking -> new UserResponse(booking.getUser())).toList();
    }

    private Event validateOwner(Event event) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (Objects.equals(userDetails.getId(), event.getCreatedBy().getId()) || userDetails.getRole() == User.Role.ADMIN) {
            return event;
        }
        throw new AccessDeniedException("You are not allowed to access this resource");
    }

    private void validateSchedule(OffsetDateTime registrationStart, OffsetDateTime registrationEnd, OffsetDateTime startTime, OffsetDateTime endTime) {
        boolean validSchedule =
                registrationStart.isBefore(registrationEnd)
                && !(registrationEnd.isAfter(startTime))
                && startTime.isBefore(endTime);
        if (!validSchedule) {
            throw new InvalidEventScheduleException();
        }
    }

    private <T> T getNewOrCurrentValue(T newValue, T currentValue) {
        return Optional.ofNullable(newValue).orElse(currentValue);
    }
}
