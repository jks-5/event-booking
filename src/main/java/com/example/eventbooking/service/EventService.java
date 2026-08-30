package com.example.eventbooking.service;

import com.example.eventbooking.dto.CreateEventRequest;
import com.example.eventbooking.dto.EventResponse;
import com.example.eventbooking.dto.UpdateEventRequest;
import com.example.eventbooking.dto.UserResponse;
import com.example.eventbooking.exception.EventNotFoundException;
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

        Event event = new Event(
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getMaxParticipants(),
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

        Optional.ofNullable(request.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(request.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(request.getLocation()).ifPresent(event::setLocation);
        Optional.ofNullable(request.getMaxParticipants()).ifPresent(event::setMaxParticipants);
        Optional.ofNullable(request.getStartTime()).ifPresent(event::setStartTime);
        Optional.ofNullable(request.getEndTime()).ifPresent(event::setEndTime);
        Optional.ofNullable(request.getRegistrationStart()).ifPresent(event::setRegistrationStart);
        Optional.ofNullable(request.getRegistrationEnd()).ifPresent(event::setRegistrationEnd);

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
}
