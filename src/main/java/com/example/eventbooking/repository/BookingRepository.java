package com.example.eventbooking.repository;

import com.example.eventbooking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByEventIdAndUserId(Long eventId, Long userId);
    int countByEventIdAndStatus(Long eventId, Booking.Status status);
    List<Booking> findByEventIdAndStatus(Long id, Booking.Status status);
    List<Booking> findByUserId(Long id);
    Optional<Booking> findFirstByEventIdAndStatusOrderByRegisteredAtAsc(Long eventId, Booking.Status status);
}
