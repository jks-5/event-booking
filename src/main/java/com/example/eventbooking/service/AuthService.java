package com.example.eventbooking.service;

import com.example.eventbooking.dto.LoginResponse;
import com.example.eventbooking.dto.LoginUserRequest;
import com.example.eventbooking.dto.RegisterUserRequest;
import com.example.eventbooking.dto.UserResponse;
import com.example.eventbooking.exception.InvalidUserException;
import com.example.eventbooking.model.CustomUserDetails;
import com.example.eventbooking.model.User;
import com.example.eventbooking.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    AuthService(UserRepository repository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterUserRequest request) {

        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new InvalidUserException("Accounts with that email already exists!");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getEmail(),
                hashedPassword,
                request.getFirstName(),
                request.getLastName()
        );

        return new UserResponse(repository.save(user));
    }

    public LoginResponse login(LoginUserRequest request) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        String jwt = jwtService.generateToken(user);

        return new LoginResponse(user.getId(), user.getEmail(), user.getRole(), jwt);
    }
}
