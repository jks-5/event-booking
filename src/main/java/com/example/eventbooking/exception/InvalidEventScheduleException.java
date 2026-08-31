package com.example.eventbooking.exception;

public class InvalidEventScheduleException extends RuntimeException {
    public InvalidEventScheduleException() {
        super("Invalid event schedule.");
    }
}
