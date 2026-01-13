package com.example.smartschedule.service;

import com.example.smartschedule.entity.User;
import com.example.smartschedule.entity.WorkingSchedule;
import com.example.smartschedule.exception.ResourceNotFoundException;
import com.example.smartschedule.repository.UserRepository;
import com.example.smartschedule.repository.WorkingScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkingScheduleService {

    private final WorkingScheduleRepository workingScheduleRepository;
    private final UserRepository userRepository;

    // Simplified service as we now have static global schedules
    // This can be used by DataInitializer or Admin if needed later

    public void initDefaultSchedule() {
        if (workingScheduleRepository.count() > 0) return;
        
        // This logic is now moved to DataInitializer as requested
    }
}