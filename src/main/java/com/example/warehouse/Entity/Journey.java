package com.example.warehouse.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "journeys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Journey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String journeyCode; // mã hành trình
    private String startLocation;
    private String endLocation;

    private LocalDate startDate;
    private LocalDate endDate;

    private String driverName;
    private String vehiclePlate;

    private String note;
}
