package com.example.eventbooking.dto;

public class GetAllEventsFilter {
    private String title;
    private String location;

    public GetAllEventsFilter() {

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
}
