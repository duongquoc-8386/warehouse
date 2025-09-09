package com.example.warehouse.Dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ScheduleRequest {
    private String title;
    private String description;
    @JsonProperty("start_location")
    private String startLocation;
    @JsonProperty("end_location")
    private String endLocation;
    private LocalDate date;
    private String status;
    private Long driverId;
    private Long truckId;
    @JsonProperty("schedule_salary")
    private BigDecimal scheduleSalary;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getTruckId() {
        return truckId;
    }

    public void setTruckId(Long truckId) {
        this.truckId = truckId;
    }

    public BigDecimal getScheduleSalary() {
        return scheduleSalary;
    }

    public void setScheduleSalary(BigDecimal scheduleSalary) {
        this.scheduleSalary = scheduleSalary;
    }
}

