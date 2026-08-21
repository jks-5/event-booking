package com.example.eventbooking.controller;

import com.example.eventbooking.dto.CreateEventRequest;
import com.example.eventbooking.dto.EventResponse;
import com.example.eventbooking.service.EventService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService service;

    EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public List<EventResponse> getAllEvents() {
        return service.getAllEvents();
    }

    @PostMapping
    public EventResponse createEvent(@Valid @RequestBody CreateEventRequest request) {
        System.out.println(request.getMaxParticipants() + request.getTitle());
        return service.createEvent(request);
    }
}
