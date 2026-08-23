package com.example.eventbooking.controller;

import com.example.eventbooking.dto.ChangeRoleRequest;
import com.example.eventbooking.dto.UserResponse;
import com.example.eventbooking.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse changeRole(@PathVariable Long id, @RequestBody ChangeRoleRequest request) {
        return service.changeRole(id, request);
    }
}
