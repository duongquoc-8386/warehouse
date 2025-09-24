package com.example.warehouse.Entity;

import com.example.warehouse.Enum.DriverStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String employeeCode; // mã nhân viên

    private String fullName;

    private String phoneNumber;

    private LocalDate dateOfBirth;

    private String note;

    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)

    private DriverStatus status;

    @Column(precision = 15, scale = 2)
    private BigDecimal basicSalary;

    @Column(precision = 15, scale = 2)
    private BigDecimal advancePayment;

    private Double allowance;
    private Double baseSalary;
    }

