package com.example.eventbooking.dto;

import com.example.eventbooking.model.Event;
import com.example.eventbooking.model.User;
import java.time.OffsetDateTime;

public class EventResponse {

    String title;
    User createdBy;
    String description;
    String location;
    Integer maxParticipants;
    OffsetDateTime startTime;
    OffsetDateTime endTime;
    OffsetDateTime registrationStart;
    OffsetDateTime registrationEnd;

    public EventResponse(Event event) {
        this.title = event.getTitle();
        this.createdBy = event.getCreatedBy();
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.maxParticipants = event.getMaxParticipants();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.registrationStart = event.getRegistrationStart();
        this.registrationEnd = event.getRegistrationEnd();
    }

    public String getTitle() {
        return title;
    }

    public Long getCreatedBy() {
        return createdBy.getId();
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public OffsetDateTime getRegistrationStart() {
        return registrationStart;
    }

    public OffsetDateTime getRegistrationEnd() {
        return registrationEnd;
    }
}
