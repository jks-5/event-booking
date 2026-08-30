package com.example.eventbooking.dto;

import com.example.eventbooking.model.Booking;
import java.time.OffsetDateTime;

public class BookingResponse {
    private Long id;
    private Long userId;
    private Long eventId;
    private OffsetDateTime registeredAt;
    private Booking.Status status;

    public BookingResponse(Booking booking) {
        this.id = booking.getId();
        this.userId = booking.getUser().getId();
        this.eventId = booking.getEvent().getId();
        this.registeredAt = booking.getRegisteredAt();
        this.status = booking.getStatus();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public OffsetDateTime getRegisteredAt() {
        return registeredAt;
    }

    public Booking.Status getStatus() {
        return status;
    }
}
