package com.example.eventbooking.dto;

import com.example.eventbooking.model.Booking;

public class MyBookingsFilter {

    private String title;
    private String location;
    private Booking.Status status;

    public MyBookingsFilter() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Booking.Status getStatus() {
        return status;
    }

    public void setStatus(Booking.Status status) {
        this.status = status;
    }
}
