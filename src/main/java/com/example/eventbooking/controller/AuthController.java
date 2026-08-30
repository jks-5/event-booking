package com.example.eventbooking.controller;

import com.example.eventbooking.dto.LoginResponse;
import com.example.eventbooking.dto.LoginUserRequest;
import com.example.eventbooking.dto.RegisterUserRequest;
import com.example.eventbooking.dto.UserResponse;
import com.example.eventbooking.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterUserRequest request) {
        return service.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginUserRequest request) {
        return service.login(request);
    }
}
