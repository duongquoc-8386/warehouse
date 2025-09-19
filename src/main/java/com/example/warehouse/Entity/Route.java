package com.example.warehouse.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "routes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"origin", "destination"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String routeCode; // route code, e.g., HN-TB

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private Double distance;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Trip> trips;


    @Column(nullable = false)
    private String routeName;   // Tên tuyến, ví dụ: Hà Nội - Thái Bình

    @Column(nullable = false)
    private Double tripPrice; // Giá tiền cho 1 chuyến

    private String note;   // Ghi chú thêm

    @Column(nullable = false, length = 150)
    private String startLocation;

    @Column(nullable = false, length = 150)
    private String endLocation;


}

