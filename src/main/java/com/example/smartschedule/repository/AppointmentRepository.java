package com.example.smartschedule.repository;

import com.example.smartschedule.entity.Appointment;
import com.example.smartschedule.entity.AppointmentStatus;
import com.example.smartschedule.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByCustomer(User customer);
    List<Appointment> findByStaff(User staff);

    @Query("SELECT a FROM Appointment a WHERE a.staff = :staff " +
           "AND a.status != 'CANCELLED' " +
           "AND ((a.startTime < :endTime) AND (a.endTime > :startTime))")
    List<Appointment> findOverlappingAppointments(@Param("staff") User staff, 
                                                  @Param("startTime") LocalDateTime startTime, 
                                                  @Param("endTime") LocalDateTime endTime);
}
