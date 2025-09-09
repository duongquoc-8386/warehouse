package com.example.warehouse.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết đến tài xế
    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    // Tháng áp dụng lương
    @Column(nullable = false)
    private String month;

    // Lương cơ bản
    @Column(nullable = false)
    private BigDecimal baseSalary;

    // Tiền ứng
    @Column(nullable = false)
    private BigDecimal advance;

    private LocalDateTime createdAt = LocalDateTime.now();
}
