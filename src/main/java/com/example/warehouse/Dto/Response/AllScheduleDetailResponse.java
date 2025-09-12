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
public class AllScheduleDetailResponse {
    private List<ScheduleDetailResponse> schedules; // tất cả lịch trình
    private double totalCostAllTrucks;             // tổng chi phí tất cả xe

    public List<ScheduleDetailResponse> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ScheduleDetailResponse> schedules) {
        this.schedules = schedules;
    }

    public double getTotalCostAllTrucks() {
        return totalCostAllTrucks;
    }

    public void setTotalCostAllTrucks(double totalCostAllTrucks) {
        this.totalCostAllTrucks = totalCostAllTrucks;
    }
}