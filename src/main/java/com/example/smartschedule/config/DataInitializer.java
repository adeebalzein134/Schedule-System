package com.example.smartschedule.config;

import com.example.smartschedule.entity.*;
import com.example.smartschedule.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final WorkingScheduleRepository workingScheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Create Admin User
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@example.com")
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);

            // Create Staff User
            User staff = User.builder()
                    .username("staff")
                    .password(passwordEncoder.encode("staff123"))
                    .email("staff@example.com")
                    .role(Role.STAFF)
                    .build();
            userRepository.save(staff);

            // Create Customer User
            User customer = User.builder()
                    .username("customer")
                    .password(passwordEncoder.encode("customer123"))
                    .email("customer@example.com")
                    .role(Role.CUSTOMER)
                    .build();
            userRepository.save(customer);

            // Create Services
            Service haircut = Service.builder()
                    .name("Haircut")
                    .duration(30)
                    .price(25.0)
                    .assignedStaff(staff)
                    .build();
            serviceRepository.save(haircut);

            Service massage = Service.builder()
                    .name("Massage")
                    .duration(60)
                    .price(50.0)
                    .assignedStaff(staff)
                    .build();
            serviceRepository.save(massage);

            // Create Global Working Schedule (Sat-Thu: 09:00-17:00, Fri: Holiday)
            for (DayOfWeek day : DayOfWeek.values()) {
                boolean isFriday = day == DayOfWeek.FRIDAY;
                WorkingSchedule schedule = WorkingSchedule.builder()
                        .dayOfWeek(day)
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(17, 0))
                        .isHoliday(isFriday)
                        .build();
                workingScheduleRepository.save(schedule);
            }

            // Create a sample appointment (optional)
            Appointment appointment = Appointment.builder()
                    .customer(customer)
                    .service(haircut)
                    .staff(staff)
                    .startTime(LocalDateTime.of(2026, 1, 10, 10, 0))
                    .endTime(LocalDateTime.of(2026, 1, 10, 10, 30))
                    .status(AppointmentStatus.PENDING)
                    .build();
            appointmentRepository.save(appointment);
        };
    }
}
