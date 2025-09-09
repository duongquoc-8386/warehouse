package com.example.warehouse.Dto.Response;

import com.example.warehouse.Entity.Driver;
import com.example.warehouse.Entity.Schedule;
import com.example.warehouse.Entity.Truck;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ScheduleResponse {
    private Long id;
    private String title;
    private LocalDate date;
    private String description;
    private String startLocation;
    private String endLocation;
    private Driver driver;
    private Truck truck;
    private BigDecimal scheduleSalary;
    private String status;
    private LocalDateTime createdAt;
    private String proofDocumentPath;

    public static ScheduleResponse fromEntity(Schedule schedule) {
        ScheduleResponse res = new ScheduleResponse();
        res.setId(schedule.getId());
        res.setTitle(schedule.getTitle());
        res.setDate(schedule.getDate());
        res.setDescription(schedule.getDescription() != null ? schedule.getDescription() : "");
        res.setStartLocation(schedule.getStartLocation() != null ? schedule.getStartLocation() : "Chưa cập nhật");
        res.setEndLocation(schedule.getEndLocation() != null ? schedule.getEndLocation() : "Chưa cập nhật");
        res.setDriver(schedule.getDriver());
        res.setTruck(schedule.getTruck());
        res.setScheduleSalary(schedule.getScheduleSalary() != null ? schedule.getScheduleSalary() : BigDecimal.ZERO);
        res.setStatus(schedule.getStatus().toString());
        res.setCreatedAt(schedule.getCreatedAt());
        res.setProofDocumentPath(schedule.getProofDocumentPath() != null ? schedule.getProofDocumentPath() : "");
        return res;
    }
    public String getProofDocumentPath() {
        return proofDocumentPath != null ? proofDocumentPath : "Chưa có tài liệu";
    }

}

