package com.example.warehouse.Dto.Response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleCostSummaryResponse {
    private List<VehicleCostResponse> vehicleCosts; // Chi phí từng xe
    private double totalCostAllTrucks;             // Tổng chi phí tất cả xe

    public List<VehicleCostResponse> getVehicleCosts() {
        return vehicleCosts;
    }

    public void setVehicleCosts(List<VehicleCostResponse> vehicleCosts) {
        this.vehicleCosts = vehicleCosts;
    }

    public double getTotalCostAllTrucks() {
        return totalCostAllTrucks;
    }

    public void setTotalCostAllTrucks(double totalCostAllTrucks) {
        this.totalCostAllTrucks = totalCostAllTrucks;
    }
}
