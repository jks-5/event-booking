package com.example.eventbooking.service;

import com.example.eventbooking.dto.RegisterUserRequest;
import com.example.eventbooking.dto.UserResponse;
import com.example.eventbooking.exception.InvalidUserException;
import com.example.eventbooking.model.User;
import com.example.eventbooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterUserRequest request;
    private User user;

    @BeforeEach
    public void setUp() {
        request = new RegisterUserRequest();
        request.setEmail("user@gmail.com");
        request.setPassword("password");
        request.setFirstName("firstName");
        request.setLastName("lastName");

        user = new User(
                "user@gmail.com",
                "password",
                "firstName",
                "lastName"
        );
    }

    @Test
    void register_shouldCreateUser_whenRequestIsValid() {
        when(passwordEncoder.encode(request.getPassword())).thenReturn("password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("user@gmail.com");
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyRegistered() {
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        assertThrows(InvalidUserException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }
}
