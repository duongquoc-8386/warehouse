package com.example.warehouse.Dto.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleCostDetail {
    private Long TripId;
    private double cost;       // Chi phí chuyến đi
    private String description; // Mô tả chuyến đi

    public Long getScheduleId() {
        return TripId;
    }

    public void setScheduleId(Long scheduleId) {
        this.TripId = getTripId();
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
