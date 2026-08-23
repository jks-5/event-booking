package com.example.eventbooking.controller;

import com.example.eventbooking.dto.CreateEventRequest;
import com.example.eventbooking.dto.EventResponse;
import com.example.eventbooking.model.CustomUserDetails;
import com.example.eventbooking.service.EventService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PreAuthorize("hasRole('ORGANIZER')")
    public EventResponse createEvent(@Valid @RequestBody CreateEventRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return service.createEvent(request, userDetails.getId());
    }
}
