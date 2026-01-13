package com.example.smartschedule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TimeSlot {
    private LocalDateTime start;
    private LocalDateTime end;
}

