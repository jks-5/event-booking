package com.example.eventbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String title;
    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    @NotNull
    private User createdBy;
    private String description;
    @NotBlank
    private String location;
    @NotNull
    @Positive
    private Integer maxParticipants;
    @NotNull
    private OffsetDateTime startTime;
    @NotNull
    private OffsetDateTime endTime;
    @NotNull
    private OffsetDateTime registrationStart;
    @NotNull
    private OffsetDateTime registrationEnd;

    @OneToMany(mappedBy = "event")
    private List<Booking> eventBookings = new ArrayList<>();

    protected Event() {

    }


    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
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

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
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

    public List<Booking> getEventBookings() {
        return eventBookings;
    }

    public void setEventBookings(List<Booking> eventBookings) {
        this.eventBookings = eventBookings;
    }
}
