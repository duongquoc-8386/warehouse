package com.example.warehouse.Repository;

import com.example.warehouse.Entity.Schedule;
import com.example.warehouse.Enum.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // Tìm tất cả Schedule theo trạng thái
    List<Schedule> findByStatus(ScheduleStatus status);

    // Tìm tất cả Schedule trong khoảng ngày
    List<Schedule> findByDateBetween(LocalDate startDate, LocalDate endDate);

    // Tìm tất cả Schedule theo trạng thái và ngày
    List<Schedule> findByStatusAndDateBetween(ScheduleStatus status, LocalDate startDate, LocalDate endDate);

    // Tìm tất cả Schedule có tiêu đề chứa từ khóa
    List<Schedule> findByTitleContainingIgnoreCase(String keyword);
}
