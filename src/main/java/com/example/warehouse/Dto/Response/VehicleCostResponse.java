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
public class VehicleCostResponse {
    private Long truckId;
    private String truckCode;
    private double totalCost; // Tổng chi phí
    private List<VehicleCostDetail> details; // Chi tiết từng chuyến

    public Long getTruckId() {
        return truckId;
    }

    public void setTruckId(Long truckId) {
        this.truckId = truckId;
    }

    public String getTruckCode() {
        return truckCode;
    }

    public void setTruckCode(String truckCode) {
        this.truckCode = truckCode;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public List<VehicleCostDetail> getDetails() {
        return details;
    }

    public void setDetails(List<VehicleCostDetail> details) {
        this.details = details;
    }
}