package com.example.eventbooking.exception;

public class RegistrationWindowClosedException extends RuntimeException {
    public RegistrationWindowClosedException(String message) {
        super(message);
    }
}
