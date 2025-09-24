package com.example.warehouse.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "trip_salary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripSalary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;   // số tiền lương cho chuyến đi

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt; // ngày tạo bản ghi

    @Column(name = "month", nullable = false, length = 7)
    private String month; // format: YYYY-MM

    // --------- Relationships ---------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;
}
