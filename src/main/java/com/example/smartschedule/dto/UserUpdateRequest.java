package com.example.smartschedule.dto;

import com.example.smartschedule.entity.Role;
import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String email;
    private Role role;
    private String password;
}

