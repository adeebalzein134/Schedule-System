package com.example.smartschedule.service;

import com.example.smartschedule.dto.AppointmentRequest;
import com.example.smartschedule.dto.TimeSlot;
import com.example.smartschedule.entity.*;
import com.example.smartschedule.exception.ResourceNotFoundException;
import com.example.smartschedule.exception.ValidationException;
import com.example.smartschedule.repository.AppointmentRepository;
import com.example.smartschedule.repository.ServiceRepository;
import com.example.smartschedule.repository.UserRepository;
import com.example.smartschedule.repository.WorkingScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final WorkingScheduleRepository workingScheduleRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Appointment bookAppointment(AppointmentRequest request, String customerUsername) {
        User customer = userRepository.findByUsername(customerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        com.example.smartschedule.entity.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        // Determine Staff from the service
        User staff = service.getAssignedStaff();

        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = startTime.plusMinutes(service.getDuration());

        // 1. Check Working Schedule (Now staff-independent)
        validateWorkingHours(startTime, endTime);

        // 2. Check Overlap
        validateNoOverlap(staff, startTime, endTime);

        Appointment appointment = Appointment.builder()
                .customer(customer)
                .service(service)
                .staff(staff)
                .startTime(startTime)
                .endTime(endTime)
                .status(AppointmentStatus.PENDING)
                .build();

        return appointmentRepository.save(appointment);
    }

    public List<TimeSlot> getAvailableSlots(Long serviceId, LocalDate date) {
        com.example.smartschedule.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        WorkingSchedule schedule = workingScheduleRepository.findByDayOfWeek(date.getDayOfWeek())
                .orElseThrow(() -> new ValidationException("No schedule defined for " + date.getDayOfWeek()));
        if (Boolean.TRUE.equals(schedule.getIsHoliday())) {
            return List.of();
        }
        int duration = service.getDuration();
        LocalDateTime open = LocalDateTime.of(date, schedule.getStartTime());
        LocalDateTime close = LocalDateTime.of(date, schedule.getEndTime());
        LocalDateTime cursor = open;
        LocalDateTime lastStart = close.minusMinutes(duration);
        List<TimeSlot> result = new ArrayList<>();
        User staff = service.getAssignedStaff();

        while (!cursor.isAfter(lastStart)) {
            LocalDateTime end = cursor.plusMinutes(duration);
            boolean available = true;
            if (staff != null) {
                List<Appointment> overlaps = appointmentRepository.findOverlappingAppointments(staff, cursor, end);
                available = overlaps.isEmpty();
            }
            if (available) {
                result.add(new TimeSlot(cursor, end));
            }
            cursor = cursor.plusMinutes(duration);
        }
        return result;
    }

    private void validateWorkingHours(LocalDateTime start, LocalDateTime end) {
        // Global schedule check - no staff dependency
        WorkingSchedule schedule = workingScheduleRepository.findByDayOfWeek(start.getDayOfWeek())
                .orElseThrow(() -> new ValidationException("No schedule defined for " + start.getDayOfWeek()));

        if (Boolean.TRUE.equals(schedule.getIsHoliday())) {
            throw new ValidationException("We are closed on " + start.getDayOfWeek());
        }

        LocalTime timeStart = start.toLocalTime();
        LocalTime timeEnd = end.toLocalTime();

        if (timeStart.isBefore(schedule.getStartTime()) || timeEnd.isAfter(schedule.getEndTime())) {
             throw new ValidationException("Appointment time is outside of working hours (" + 
                     schedule.getStartTime() + " - " + schedule.getEndTime() + ")");
        }
    }

    private void validateNoOverlap(User staff, LocalDateTime start, LocalDateTime end) {
        // Only check for staff overlap if a staff member is actually assigned
        if (staff != null) {
            List<Appointment> overlaps = appointmentRepository.findOverlappingAppointments(staff, start, end);
            if (!overlaps.isEmpty()) {
                throw new ValidationException("Time slot is already booked for this staff member");
            }
        }
        // If no staff is assigned to the service, we don't need to check for staff-specific overlaps.
        // The system assumes any available capacity can handle it.
    }
    
    public List<Appointment> getAppointmentsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (user.getRole() == Role.STAFF) {
            return appointmentRepository.findByStaff(user);
        } else if (user.getRole() == Role.CUSTOMER) {
            return appointmentRepository.findByCustomer(user);
        } else {
            return appointmentRepository.findAll();
        }
    }

    @Transactional
    public Appointment updateStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        
        appointment.setStatus(status);
        Appointment saved = appointmentRepository.save(appointment);

        // Notify Customer
        String notificationMessage = "Your appointment for " + saved.getService().getName() + " is now " + status;
        messagingTemplate.convertAndSend("/topic/notifications/" + saved.getCustomer().getUsername(), notificationMessage);
        
        return saved;
    }
}
