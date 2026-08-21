package com.example.eventbooking.service;

import com.example.eventbooking.dto.CreateEventRequest;
import com.example.eventbooking.dto.EventResponse;
import com.example.eventbooking.model.Event;
import com.example.eventbooking.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventRepository repository;

    EventService(EventRepository repository) {
        this.repository = repository;
    }

    public List<EventResponse> getAllEvents() {
        return repository.findAll().stream().map(EventResponse::new).toList();
    }

    public EventResponse createEvent(CreateEventRequest request) {
        Event event = new Event(
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getMaxParticipants(),
                request.getStartTime(),
                request.getEndTime(),
                request.getRegistrationStart(),
                request.getRegistrationEnd()
        );

        return new EventResponse(repository.save(event));
    }
}
