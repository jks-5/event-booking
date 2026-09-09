package com.example.eventbooking.controller;

import com.example.eventbooking.dto.*;
import com.example.eventbooking.model.CustomUserDetails;
import com.example.eventbooking.service.EventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
@Validated
public class EventController {
    private final EventService service;

    EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public Page<EventResponse> getAllEvents(@PageableDefault(sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable,
                                            @ModelAttribute GetAllEventsFilter filter) {
        return service.getAllEvents(pageable, filter);
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

    @GetMapping("/{id}/participants")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public Page<UserResponse> getParticipants(@PathVariable Long id,
                                              @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                              @ModelAttribute GetParticipantsFilter filter) {
        return service.getParticipants(id, pageable, filter);
    }
}
