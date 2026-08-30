package com.example.eventbooking.exception;

public class MaxCapacityReachedException extends RuntimeException {
    public MaxCapacityReachedException() {
        super("The maximum number of participants has already been reached.");
    }
}
