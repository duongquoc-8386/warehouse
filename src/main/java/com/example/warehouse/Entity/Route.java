package com.example.warehouse.Entity;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "routes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_code", nullable = false, unique = true)
    private String routeCode;

    @Column(name = "route_name", nullable = false)
    private String routeName;

    @Column(name = "trip_price", nullable = false)
    private Double tripPrice;

    @Column(name = "note")
    private String note;
}
