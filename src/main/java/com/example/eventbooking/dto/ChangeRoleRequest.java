package com.example.eventbooking.dto;

public class ChangeRoleRequest {

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
