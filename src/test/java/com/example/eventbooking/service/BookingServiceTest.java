package com.example.eventbooking.service;

import com.example.eventbooking.dto.BookingResponse;
import com.example.eventbooking.exception.BookingAlreadyCancelledException;
import com.example.eventbooking.exception.MaxCapacityReachedException;
import com.example.eventbooking.exception.RegistrationWindowClosedException;
import com.example.eventbooking.model.Booking;
import com.example.eventbooking.model.Event;
import com.example.eventbooking.model.User;
import com.example.eventbooking.repository.BookingRepository;
import com.example.eventbooking.repository.EventRepository;
import com.example.eventbooking.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BookingService bookingService;

    private Event event;
    private User user;

    @BeforeEach
    public void setUp() {
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
                new User(
                        "organizer@gmai.com",
                        "password",
                        "firstName",
                        "lastName"
                )
        );

        user = new User(
                "user@gmai.com",
                "password",
                "firstName",
                "lastName"
        );

        ReflectionTestUtils.setField(event, "id", 1L);
        ReflectionTestUtils.setField(user, "id", 1L);
    }

    @Test
    void bookEvent_shouldReturnConfirmed_whenSpotsAvailable() {
        when(eventRepository.findWithOptimisticLockById(event.getId())).thenReturn(Optional.of(event));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.countByEventIdAndStatus(event.getId(), Booking.Status.CONFIRMED)).thenReturn(4);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        BookingResponse response = bookingService.bookEvent(event.getId(), user.getId());

        assertThat(response.getStatus()).isEqualTo(Booking.Status.CONFIRMED);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void bookEvent_shouldReturnWaitlisted_whenNoSpotsAvailable() {
        when(eventRepository.findWithOptimisticLockById(event.getId())).thenReturn(Optional.of(event));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.countByEventIdAndStatus(event.getId(), Booking.Status.CONFIRMED)).thenReturn(10);
        when(bookingRepository.countByEventIdAndStatus(event.getId(), Booking.Status.WAITLISTED)).thenReturn(1);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        BookingResponse response = bookingService.bookEvent(event.getId(), user.getId());

        assertThat(response.getStatus()).isEqualTo(Booking.Status.WAITLISTED);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void bookEvent_shouldThrowException_whenEventAndWaitlistFull() {
        when(eventRepository.findWithOptimisticLockById(event.getId())).thenReturn(Optional.of(event));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.countByEventIdAndStatus(event.getId(), Booking.Status.CONFIRMED)).thenReturn(10);
        when(bookingRepository.countByEventIdAndStatus(event.getId(), Booking.Status.WAITLISTED)).thenReturn(3);

        assertThrows(MaxCapacityReachedException.class, () -> bookingService.bookEvent(event.getId(), user.getId()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void bookEvent_shouldThrowException_whenRegistrationWindowClosed() {
        OffsetDateTime now = OffsetDateTime.now();
        event.setRegistrationStart(now.minusDays(10));
        event.setRegistrationEnd(now.minusDays(5));

        when(eventRepository.findWithOptimisticLockById(event.getId())).thenReturn(Optional.of(event));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(RegistrationWindowClosedException.class, () -> bookingService.bookEvent(event.getId(), user.getId()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void cancelBooking_shouldSetStatusToCancelled_whenBookingStatusIsConfirmedOrWaitlisted() {
        Booking booking = new Booking(user, event, Booking.Status.CONFIRMED);

        when(eventRepository.findWithOptimisticLockById(event.getId())).thenReturn(Optional.of(event));
        when(bookingRepository.findByEventIdAndUserId(event.getId(), user.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        BookingResponse response = bookingService.cancelBooking(event.getId(), user.getId());

        assertThat(response.getStatus()).isEqualTo(Booking.Status.CANCELLED);
        assertThat(booking.getStatus()).isEqualTo(Booking.Status.CANCELLED);
    }

    @Test
    void cancelBooking_shouldPromoteWaitlistedBooking_whenBookingStatusIsConfirmed() {
        Booking booking = new Booking(user, event, Booking.Status.CONFIRMED);
        Booking waitlistedBooking = new Booking(
                new User(
                "waitlisteduser@gmai.com",
                "password",
                "firstName",
                "lastName"),
                event,
                Booking.Status.WAITLISTED
        );

        when(eventRepository.findWithOptimisticLockById(event.getId())).thenReturn(Optional.of(event));
        when(bookingRepository.findByEventIdAndUserId(event.getId(), user.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.findFirstByEventIdAndStatusOrderByRegisteredAtAsc(event.getId(), Booking.Status.WAITLISTED)).thenReturn(Optional.of(waitlistedBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        BookingResponse response = bookingService.cancelBooking(event.getId(), user.getId());

        assertThat(response.getStatus()).isEqualTo(Booking.Status.CANCELLED);
        assertThat(booking.getStatus()).isEqualTo(Booking.Status.CANCELLED);
        assertThat(waitlistedBooking.getStatus()).isEqualTo(Booking.Status.CONFIRMED);
    }

    @Test
    void cancelBooking_shouldThrowException_whenBookingIsAlreadyCancelled() {
        Booking booking = new Booking(user, event, Booking.Status.CANCELLED);

        when(eventRepository.findWithOptimisticLockById(event.getId())).thenReturn(Optional.of(event));
        when(bookingRepository.findByEventIdAndUserId(event.getId(), user.getId())).thenReturn(Optional.of(booking));

        assertThrows(BookingAlreadyCancelledException.class, () -> bookingService.cancelBooking(event.getId(), user.getId()));
    }
}

