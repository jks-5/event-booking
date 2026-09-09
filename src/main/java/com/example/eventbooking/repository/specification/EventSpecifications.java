package com.example.eventbooking.repository.specification;

import com.example.eventbooking.model.Event;
import org.springframework.data.jpa.domain.Specification;

public class EventSpecifications {

    public static Specification<Event> hasTitle(String title) {
        return ((root, query, criteriaBuilder) ->
                title != null && !title.isBlank() ? criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title + "%") : null);
    }

    public static Specification<Event> hasLocation(String location) {
        return ((root, query, criteriaBuilder) ->
                location != null && !location.isBlank() ? criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), "%" + location + "%") : null);
    }
}
