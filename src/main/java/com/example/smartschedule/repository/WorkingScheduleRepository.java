package com.example.smartschedule.repository;

import com.example.smartschedule.entity.User;
import com.example.smartschedule.entity.WorkingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkingScheduleRepository extends JpaRepository<WorkingSchedule, Long> {
    Optional<WorkingSchedule> findByDayOfWeek(DayOfWeek dayOfWeek);
}
