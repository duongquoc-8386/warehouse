package com.example.warehouse.Dto.Response;

import com.example.warehouse.Entity.Trip;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TripResponse {
    private Long id;
    private String title;
    private LocalDate date;
    private String description;
    private String startLocation;
    private String endLocation;
    private String driverName;
    private String truckName;
    private BigDecimal scheduleSalary;
    private String status;
    private LocalDateTime createdAt;
    private Double cost;
    private String costFormatted;
    private String proofDocumentPath;

    public static TripResponse fromEntity(Trip trip) {
        if (trip == null) return null;

        TripResponse res = new TripResponse();
        res.setId(trip.getId());
        res.setTitle(trip.getTitle());
        res.setDate(trip.getStartTime() != null ? trip.getStartTime().toLocalDate() : null);
        res.setDescription(trip.getDescription() != null ? trip.getDescription() : "");
        res.setStartLocation(trip.getRoute() != null ? trip.getRoute().getStartLocation() : "Chưa cập nhật");
        res.setEndLocation(trip.getRoute() != null ? trip.getRoute().getEndLocation() : "Chưa cập nhật");

        // Chỉ lấy các trường kiểu String, tránh gửi nguyên entity Hibernate
        res.setDriverName(trip.getDriver() != null ? trip.getDriver().getFullName() : "Chưa cập nhật");
        res.setTruckName(trip.getTruck() != null ? trip.getTruck().getLicensePlate() : "Chưa cập nhật");


        // Lương mặc định
        res.setScheduleSalary(BigDecimal.ZERO);

        res.setStatus(trip.getStatus() != null ? trip.getStatus().toString() : "Chưa cập nhật");
        res.setCreatedAt(trip.getCreatedAt());
        res.setCost(trip.getCost());
        res.setCostFormatted(trip.getCost() != null ? String.format("%,.0f VND", trip.getCost()) : "0 VND");
        res.setProofDocumentPath(trip.getProofDocumentPath() != null ? trip.getProofDocumentPath() : "");

        return res;
    }
}

