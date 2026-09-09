package com.example.eventbooking.repository.specification;

import com.example.eventbooking.model.Booking;
import org.springframework.data.jpa.domain.Specification;

public class BookingSpecifications {

    public static Specification<Booking> hasUserId(Long userId) {
        return ((root, query, criteriaBuilder) ->
                userId != null ? criteriaBuilder.equal(root.get("user").get("id"), userId) : null);
    }

    public static Specification<Booking> hasFirstName(String firstName) {
        return ((root, query, criteriaBuilder) ->
                firstName != null && !firstName.isBlank() ? criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("firstName")), "%" + firstName + "%") : null);
    }

    public static Specification<Booking> hasLastName(String lastName) {
        return ((root, query, criteriaBuilder) ->
                lastName != null && !lastName.isBlank() ? criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("lastName")), "%" + lastName + "%") : null);
    }

    public static Specification<Booking> hasEventTitle(String eventTitle) {
        return ((root, query, criteriaBuilder) ->
                eventTitle != null && !eventTitle.isBlank() ? criteriaBuilder.like(criteriaBuilder.lower(root.get("event").get("title")), "%" + eventTitle + "%") : null);
    }

    public static Specification<Booking> hasEventLocation(String eventLocation) {
        return ((root, query, criteriaBuilder) ->
                eventLocation != null && !eventLocation.isBlank() ? criteriaBuilder.like(criteriaBuilder.lower(root.get("event").get("location")), "%" + eventLocation + "%") : null);
    }

    public static Specification<Booking> hasEventId(Long eventId) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("event").get("id"), eventId));
    }

    public static Specification<Booking> hasStatus(Booking.Status status) {
        return ((root, query, criteriaBuilder) ->
                status != null ? criteriaBuilder.equal(root.get("status"), status) : null);
    }
}
