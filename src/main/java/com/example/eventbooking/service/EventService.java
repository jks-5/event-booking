package com.example.eventbooking.service;

import com.example.eventbooking.dto.CreateEventRequest;
import com.example.eventbooking.dto.EventResponse;
import com.example.eventbooking.exception.UserNotFoundException;
import com.example.eventbooking.model.Event;
import com.example.eventbooking.model.User;
import com.example.eventbooking.repository.EventRepository;
import com.example.eventbooking.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventRepository repository;
    private final UserRepository userRepository;

    EventService(EventRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
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
}
