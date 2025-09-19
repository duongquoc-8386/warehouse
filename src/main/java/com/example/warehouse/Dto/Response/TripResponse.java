package com.example.warehouse.Dto.Response;

import com.example.warehouse.Entity.Driver;
import com.example.warehouse.Entity.Truck;
import com.example.warehouse.Entity.Trip;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TripResponse {
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
    private Double cost;
    private String costFormatted;
    private String proofDocumentPath;

    public static TripResponse fromEntity(Trip trip) {
        TripResponse res = new TripResponse();
        res.setId(trip.getId());
        res.setTitle(trip.getTitle());

        // Dùng startTime làm ngày
        res.setDate(trip.getStartTime() != null ? trip.getStartTime().toLocalDate() : null);

        res.setDescription(trip.getDescription() != null ? trip.getDescription() : "");

        // start/end location hiện không có trong Trip -> tạm set bằng Route
        res.setStartLocation(trip.getRoute() != null ? trip.getRoute().getStartLocation() : "Chưa cập nhật");
        res.setEndLocation(trip.getRoute() != null ? trip.getRoute().getEndLocation() : "Chưa cập nhật");

        res.setDriver(trip.getDriver());
        res.setTruck(trip.getTruck());

        // Trip chưa có lương riêng -> gán mặc định 0
        res.setScheduleSalary(BigDecimal.ZERO);

        res.setStatus(trip.getStatus() != null ? trip.getStatus().toString() : "Chưa cập nhật");
        res.setCreatedAt(trip.getCreatedAt());
        res.setCost(trip.getCost());
        res.setCostFormatted(trip.getCost() != null ? String.format("%,.0f VND", trip.getCost()) : "0 VND");
        res.setProofDocumentPath(trip.getProofDocumentPath() != null ? trip.getProofDocumentPath() : "");
        return res;
    }

    public String getProofDocumentPath() {
        return proofDocumentPath != null ? proofDocumentPath : "Chưa có tài liệu";
    }
}
