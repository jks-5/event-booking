package com.example.eventbooking.service;

import com.example.eventbooking.model.CustomUserDetails;
import com.example.eventbooking.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    @InjectMocks
    private JwtService jwtService;

    private User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", "/yXM6KgGzLJ7FAghnmlF2Lm/eAuXEGYxlZa83kNqhAU=");

        user = new User(
                "user@gmail.com",
                "password",
                "firstName",
                "lastName"
        );
    }

    @Test
    void generateToken_shouldReturnToken_whenUserIsValid() {
        String response = jwtService.generateToken(user);

        assertThat(response).isNotNull();
    }

    @Test
    void isTokenValid_shouldReturnTrue_whenTokenIsValid() {
        String token = jwtService.generateToken(user);
        UserDetails userDetails = new CustomUserDetails(user);

        boolean response = jwtService.isTokenValid(token, userDetails);

        assertThat(response).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenIsExpired() {
        String expiredToken = Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor("/yXM6KgGzLJ7FAghnmlF2Lm/eAuXEGYxlZa83kNqhAU=".getBytes(StandardCharsets.UTF_8)))
                .compact();

        UserDetails userDetails = new CustomUserDetails(user);

        boolean response = jwtService.isTokenValid(expiredToken, userDetails);

        assertThat(response).isFalse();
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenEmailDoesntMatch() {
        String token = jwtService.generateToken(user);
        user.setEmail("invalid@gmail.com");
        UserDetails userDetails = new CustomUserDetails(user);

        boolean response = jwtService.isTokenValid(token, userDetails);

        assertThat(response).isFalse();
    }
}
