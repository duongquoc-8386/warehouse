package com.example.warehouse.Entity;

import com.example.warehouse.Enum.TruckStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
public class Truck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Liên kết với Driver
    @OneToOne

    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    private Driver driver;

    @Column(nullable = false, unique = true)
    private String licensePlate; // Biển số xe
    private double capacity;     // Dung tích
    @Enumerated(EnumType.STRING)
    private TruckStatus status;       // "AVAILABLE": có sẵn, "IN_USE": đang sd, "MAINTENANCE":bảo trì

    public TruckStatus getStatus() {
        return status;
    }

    public void setStatus(TruckStatus status) {
        this.status = status;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

