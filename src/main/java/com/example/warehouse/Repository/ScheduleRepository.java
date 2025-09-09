package com.example.warehouse.Repository;


import com.example.warehouse.Entity.Schedule;
import com.example.warehouse.Enum.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByStatus(ScheduleStatus status);
    @Query("SELECT s FROM Schedule s WHERE s.driver.id = :driverId AND FUNCTION('DATE_FORMAT', s.date, '%Y-%m') = :month")
    List<Schedule> findByDriverAndMonth(@Param("driverId") Long driverId, @Param("month") String month);
}
