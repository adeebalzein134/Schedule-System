package com.example.smartschedule.dto;

import com.example.smartschedule.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 4) String password,
        @NotBlank @Email String email,
        Role role   // optional – defaults to CUSTOMER in service
) {}