package com.example.eventbooking.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UniqueEventIdUserId", columnNames = {"user_id", "event_id"})
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @Column(nullable = false)
    private OffsetDateTime registeredAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;


    protected Booking() {

    }

    public Booking(User user, Event event) {
        setUser(user);
        setEvent(event);
        setRegisteredAt(OffsetDateTime.now());
        setStatus(Status.CONFIRMED);
    }

    public enum Status {CONFIRMED, CANCELLED}

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public OffsetDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(OffsetDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
