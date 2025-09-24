package com.example.warehouse.Entity;

import com.example.warehouse.Enum.TruckStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "truck")
public class Truck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String licensePlate;

    private String type; // "TRACTOR", "TRUCK"




    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mooc_id", nullable = true)
    @JsonIgnoreProperties
    private Mooc mooc;

    private double capacity;

    @Enumerated(EnumType.STRING)
    private TruckStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    @JsonIgnoreProperties
    private Driver driver;




}
