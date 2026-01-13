package com.example.smartschedule.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequest {
    private Long serviceId;
    private LocalDateTime startTime;
}
