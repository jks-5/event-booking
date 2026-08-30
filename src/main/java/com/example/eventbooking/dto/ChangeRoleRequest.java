package com.example.eventbooking.dto;

import jakarta.validation.constraints.NotNull;

public class ChangeRoleRequest {

    @NotNull
    private ChangedRole role;

    public ChangeRoleRequest() {
    }

    public enum ChangedRole {USER, ORGANIZER}

    public ChangedRole getRole() {
        return role;
    }

    public void setRole(ChangedRole role) {
        this.role = role;
    }
}
