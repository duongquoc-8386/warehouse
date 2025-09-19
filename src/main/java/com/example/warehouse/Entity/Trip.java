package com.example.warehouse.Entity;

import com.example.warehouse.Enum.TripStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trips ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title; // ví dụ: Hanoi - Thai Binh

    @Column(nullable = false)
    private Double cost; // chi phí chuyến đi

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(length = 500)
    private String description;

    @Column(name = "proof_document_path", length = 255)
    private String proofDocumentPath; // đường dẫn file chứng từ

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TripStatus status; // PENDING, APPROVED, REJECTED, SUBMITTED

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ========== Relationships ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id", nullable = false)
    private Truck truck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
}
