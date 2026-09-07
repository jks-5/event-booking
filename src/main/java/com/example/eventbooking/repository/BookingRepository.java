package com.example.eventbooking.repository;

import com.example.eventbooking.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByEventIdAndUserId(Long eventId, Long userId);
    int countByEventIdAndStatus(Long eventId, Booking.Status status);
    Page<Booking> findByEventIdAndStatus(Long id, Booking.Status status, Pageable pageable);
    Optional<Booking> findFirstByEventIdAndStatusOrderByRegisteredAtAsc(Long eventId, Booking.Status status);
    Page<Booking> findByUserId(Long userId, Pageable pageable);
}
