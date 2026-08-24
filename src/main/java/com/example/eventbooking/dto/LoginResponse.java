package com.example.eventbooking.dto;

import com.example.eventbooking.model.User;

public class LoginResponse {
    private Long id;
    private String email;
    private User.Role role;
    private String jwt;

    public LoginResponse(Long id, String email, User.Role role, String jwt) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.jwt = jwt;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public User.Role getRole() {
        return role;
    }

    public String getJwt() {
        return jwt;
    }
}
