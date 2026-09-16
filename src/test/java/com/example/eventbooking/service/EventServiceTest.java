package com.example.eventbooking.service;

import com.example.eventbooking.dto.CreateEventRequest;
import com.example.eventbooking.dto.EventResponse;
import com.example.eventbooking.exception.InvalidEventScheduleException;
import com.example.eventbooking.model.CustomUserDetails;
import com.example.eventbooking.model.Event;
import com.example.eventbooking.model.User;
import com.example.eventbooking.repository.BookingRepository;
import com.example.eventbooking.repository.EventRepository;
import com.example.eventbooking.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private EventService eventService;

    private CreateEventRequest request;
    private User user;
    private User organizer;
    private Event event;

    @BeforeEach
    public void setUp() {
        request = new CreateEventRequest();

        request.setTitle("test event");
        request.setDescription("test");
        request.setLocation("Berlin");
        request.setMaxParticipants(10);
        request.setWaitlistSpots(3);
        request.setStartTime(OffsetDateTime.of(2100, 10, 20, 0, 0, 0, 0, ZoneOffset.UTC));
        request.setEndTime(OffsetDateTime.of(2100, 10, 21, 0, 0, 0, 0, ZoneOffset.UTC));
        request.setRegistrationStart(OffsetDateTime.of(2026, 9, 15, 0, 0, 0, 0, ZoneOffset.UTC));
        request.setRegistrationEnd(OffsetDateTime.of(2100, 10, 15, 0, 0, 0, 0, ZoneOffset.UTC));

        user = new User(
                "user@gmai.com",
                "password",
                "firstName",
                "lastName"
        );
        ReflectionTestUtils.setField(user, "id", 1L);

        organizer = new User(
                "organizer@gmai.com",
                "password",
                "firstName",
                "lastName"
        );

        event = new Event(
                "test event",
                "test",
                "Berlin",
                10,
                3,
                OffsetDateTime.of(2100, 10, 20, 0, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2100, 10, 21, 0, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 15, 0, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2100, 10, 15, 0, 0, 0, 0, ZoneOffset.UTC),
                organizer
        );

        ReflectionTestUtils.setField(event, "id", 1L);
        ReflectionTestUtils.setField(organizer, "id", 2L);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createEvent_shouldCreateEvent_whenScheduleIsValid() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0));
        EventResponse response = eventService.createEvent(request, user.getId());

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("test event");
    }

    @Test
    void createEvent_shouldThrowException_whenRegistrationStartIsAfterRegistrationEnd() {
        request.setRegistrationStart(OffsetDateTime.of(2100, 10, 16, 0, 0, 0, 0, ZoneOffset.UTC));
        request.setRegistrationEnd(OffsetDateTime.of(2100, 10, 15, 0, 0, 0, 0, ZoneOffset.UTC));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(InvalidEventScheduleException.class, () -> eventService.createEvent(request, user.getId()));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void createEvent_shouldThrowException_whenRegistrationEndIsAfterEventStart() {
        request.setRegistrationEnd(OffsetDateTime.of(2100, 10, 21, 0, 0, 0, 0, ZoneOffset.UTC));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(InvalidEventScheduleException.class, () -> eventService.createEvent(request, user.getId()));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void createEvent_shouldThrowException_whenStartTimeIsAfterEndTime() {
        request.setStartTime(OffsetDateTime.of(2100, 10, 21, 0, 0, 0, 0, ZoneOffset.UTC));
        request.setEndTime(OffsetDateTime.of(2100, 10, 20, 0, 0, 0, 0, ZoneOffset.UTC));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(InvalidEventScheduleException.class, () -> eventService.createEvent(request, user.getId()));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void deleteEvent_shouldDeleteEvent_whenUserIsOwner() {
        CustomUserDetails userDetails = new CustomUserDetails(organizer);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        eventService.deleteEvent(event.getId());

        verify(eventRepository).delete(event);
    }

    @Test
    void deleteEvent_shouldThrowException_whenUserIsNotOwner() {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        assertThrows(AccessDeniedException.class, () -> eventService.deleteEvent(event.getId()));
        verify(eventRepository, never()).delete(any(Event.class));
    }
}
