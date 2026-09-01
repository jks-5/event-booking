package com.example.eventbooking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.OffsetDateTime;

public class UpdateEventRequest {

    private String title;
    private String description;
    private String location;
    @Positive
    private Integer maxParticipants;
    @PositiveOrZero
    private Integer waitlistSpots;
    @Future
    private OffsetDateTime startTime;
    @Future
    private OffsetDateTime endTime;
    @FutureOrPresent
    private OffsetDateTime registrationStart;
    @FutureOrPresent
    private OffsetDateTime registrationEnd;

    UpdateEventRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }

    public OffsetDateTime getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart(OffsetDateTime registrationStart) {
        this.registrationStart = registrationStart;
    }

    public OffsetDateTime getRegistrationEnd() {
        return registrationEnd;
    }

    public void setRegistrationEnd(OffsetDateTime registrationEnd) {
        this.registrationEnd = registrationEnd;
    }

    public Integer getWaitlistSpots() {
        return waitlistSpots;
    }

    public void setWaitlistSpots(Integer waitlistSpots) {
        this.waitlistSpots = waitlistSpots;
    }
}
