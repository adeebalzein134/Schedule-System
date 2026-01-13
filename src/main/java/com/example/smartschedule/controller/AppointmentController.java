package com.example.smartschedule.controller;

import com.example.smartschedule.dto.AppointmentRequest;
import com.example.smartschedule.entity.Appointment;
import com.example.smartschedule.entity.AppointmentStatus;
import com.example.smartschedule.dto.TimeSlot;
import com.example.smartschedule.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public Appointment bookAppointment(@RequestBody AppointmentRequest request, Authentication authentication) {
        return appointmentService.bookAppointment(request, authentication.getName());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Appointment> getAppointments(Authentication authentication) {
        return appointmentService.getAppointmentsForUser(authentication.getName());
    }

    @GetMapping("/availability")
    @PreAuthorize("isAuthenticated()")
    public List<TimeSlot> getAvailableSlots(
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return appointmentService.getAvailableSlots(serviceId, date);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Appointment updateStatus(@PathVariable Long id, @RequestParam AppointmentStatus status, Authentication authentication) {
        // In a real app, we should check if the user owns the appointment or has rights.
        // For now, relying on Role check and trusting Service logic if expanded.
        // Ideally Service should verify ownership for Customer.
        return appointmentService.updateStatus(id, status);
    }
}
