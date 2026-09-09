package com.example.eventbooking.controller;

import com.example.eventbooking.dto.BookingResponse;
import com.example.eventbooking.dto.MyBookingsFilter;
import com.example.eventbooking.dto.MyBookingsResponse;
import com.example.eventbooking.model.CustomUserDetails;
import com.example.eventbooking.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
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

    @GetMapping("/users/me/bookings")
    public Page<MyBookingsResponse> getMyBookings(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @PageableDefault(sort = "registeredAt", direction = Sort.Direction.DESC) Pageable pageable,
                                                  @ModelAttribute MyBookingsFilter filter) {
        return service.getMyBookings(userDetails.getId(), pageable, filter);
    }
}
