package com.example.warehouse.Entity;

import com.example.warehouse.Enum.ScheduleStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDate date;
    private String description;
    private String startLocation;
    private String endLocation;


    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "truck_id", nullable = false)
    private Truck truck;

    // Lương hiệu suất cho chuyến này
    @Column(nullable = false)
    private BigDecimal scheduleSalary = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();

    private String proofDocumentPath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }

    public BigDecimal getScheduleSalary() {
        return scheduleSalary;
    }

    public void setScheduleSalary(BigDecimal scheduleSalary) {
        this.scheduleSalary = scheduleSalary;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getProofDocumentPath() {
        return proofDocumentPath;
    }

    public void setProofDocumentPath(String proofDocumentPath) {
        this.proofDocumentPath = proofDocumentPath;
    }
}