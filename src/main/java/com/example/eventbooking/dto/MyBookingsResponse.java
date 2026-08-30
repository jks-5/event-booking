package com.example.eventbooking.dto;

import com.example.eventbooking.model.Booking;

import java.time.OffsetDateTime;

public class MyBookingsResponse {
    private Long bookingId;
    private Booking.Status status;
    private OffsetDateTime registeredAt;
    private EventResponse event;

    public MyBookingsResponse(Booking booking, EventResponse eventResponse) {
        this.bookingId = booking.getId();
        this.status = booking.getStatus();
        this.registeredAt = booking.getRegisteredAt();
        this.event = eventResponse;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public Booking.Status getStatus() {
        return status;
    }

    public OffsetDateTime getRegisteredAt() {
        return registeredAt;
    }

    public EventResponse getEvent() {
        return event;
    }
}
