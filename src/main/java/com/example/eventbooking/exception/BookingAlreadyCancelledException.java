package com.example.eventbooking.exception;

public class BookingAlreadyCancelledException extends RuntimeException {
    public BookingAlreadyCancelledException() {
        super("Booking was already cancelled.");
    }
}
