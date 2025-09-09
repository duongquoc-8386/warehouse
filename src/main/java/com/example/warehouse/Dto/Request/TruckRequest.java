package com.example.warehouse.Dto.Request;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TruckRequest {
    private String licensePlate;
    private double capacity;
    private String status;      // TruckStatus (String)
    private Long driverId;      // id của Driver
}
