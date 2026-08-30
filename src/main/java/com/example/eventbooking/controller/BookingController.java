package com.example.eventbooking.controller;

import com.example.eventbooking.dto.BookingResponse;
import com.example.eventbooking.model.CustomUserDetails;
import com.example.eventbooking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookingController {

    private final BookingService service;

    BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping("/events/{id}/booking")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse bookEvent(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return service.bookEvent(id, userDetails.getId());
    }

    @PatchMapping("/events/{id}/booking")
    public BookingResponse cancelBooking(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return service.cancelBooking(id, userDetails.getId());
    }
}
