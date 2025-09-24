package com.example.warehouse.Dto.Response;

import com.example.warehouse.Entity.Truck;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TruckResponse {
    private Long id;
    private String licensePlate;
    private String status;

    public static TruckResponse fromEntity(Truck truck) {
        if (truck == null) return null;
        return TruckResponse.builder()
                .id(truck.getId())
                .licensePlate(truck.getLicensePlate() != null ? truck.getLicensePlate() : "Chưa cập nhật")
                .status(truck.getStatus() != null ? truck.getStatus().toString() : "Chưa cập nhật")
                .build();
    }
}

