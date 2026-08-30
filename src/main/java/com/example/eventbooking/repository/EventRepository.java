package com.example.eventbooking.repository;

import com.example.eventbooking.model.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<Event> findWithVersionIncrementById(Long id);
}
