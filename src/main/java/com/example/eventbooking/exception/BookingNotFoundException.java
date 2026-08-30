package com.example.eventbooking.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException() {
        super("Booking not found.");
    }
}
