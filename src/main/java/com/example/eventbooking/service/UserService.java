package com.example.eventbooking.service;

import com.example.eventbooking.dto.ChangeRoleRequest;
import com.example.eventbooking.dto.UserResponse;
import com.example.eventbooking.exception.UserNotFoundException;
import com.example.eventbooking.model.User;
import com.example.eventbooking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository repository;

    UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserResponse changeRole(Long id, ChangeRoleRequest request) {
        User user = repository.findById(id).orElseThrow(UserNotFoundException::new);

        user.setRole(User.Role.valueOf(request.getRole().name()));

        return new UserResponse(repository.save(user));
    }
}
