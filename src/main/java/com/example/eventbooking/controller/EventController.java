package com.example.eventbooking.controller;

import com.example.eventbooking.dto.CreateEventRequest;
import com.example.eventbooking.dto.EventResponse;
import com.example.eventbooking.dto.UpdateEventRequest;
import com.example.eventbooking.model.CustomUserDetails;
import com.example.eventbooking.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(@Valid @RequestBody CreateEventRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return service.createEvent(request, userDetails.getId());
    }

    @GetMapping("/{id}")
    public EventResponse getEventById(@PathVariable Long id) {
        return service.getEventById(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public EventResponse updateEvent(@PathVariable Long id, @Valid @RequestBody UpdateEventRequest request) {
        return service.updateEvent(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public ResponseEntity<Void> deleteEventById(@PathVariable Long id) {
        service.deleteEvent(id);

        return ResponseEntity.noContent().build();
    }
}
