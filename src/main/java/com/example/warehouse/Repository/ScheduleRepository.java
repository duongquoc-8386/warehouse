package com.example.warehouse.Repository;

import com.example.warehouse.Entity.Schedule;
import com.example.warehouse.Enum.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // Lấy lịch trình của 1 xe giữa 2 ngày
    List<Schedule> findByTruckIdAndDateBetween(Long truckId, LocalDateTime from, LocalDateTime to);

    // Lấy tất cả lịch trình giữa 2 ngày
    List<Schedule> findAllByDateBetween(LocalDateTime from, LocalDateTime to);

    // Lấy lịch trình của 1 tài xế giữa 2 ngày
    List<Schedule> findByDriverIdAndDateBetween(Long driverId, LocalDateTime from, LocalDateTime to);
    List<Schedule> findByStatus(ScheduleStatus status);
    List<Schedule> findByDriverId(Long driverId);
    List<Schedule> findByTruckId(Long truckId);

}
