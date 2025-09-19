package com.example.warehouse.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripReportResponse {
    private Long driverId;
    private String driverName;
    private long tripCount;
    private double totalTripCost;
}

