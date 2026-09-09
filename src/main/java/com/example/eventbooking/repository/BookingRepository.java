package com.example.eventbooking.repository;

import com.example.eventbooking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    Optional<Booking> findByEventIdAndUserId(Long eventId, Long userId);
    int countByEventIdAndStatus(Long eventId, Booking.Status status);
    Optional<Booking> findFirstByEventIdAndStatusOrderByRegisteredAtAsc(Long eventId, Booking.Status status);
}
