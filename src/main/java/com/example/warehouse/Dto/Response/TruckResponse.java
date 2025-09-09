package com.example.warehouse.Dto.Response;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TruckResponse {
    private Long id;
    private String licensePlate;
    private double capacity;
    private String status;
    private Long driverId;
    private String driverName;
}
