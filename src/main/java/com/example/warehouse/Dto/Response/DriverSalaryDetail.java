package com.example.warehouse.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DriverSalaryDetail {
    private Long scheduleId;
    private double routeSalary;   // Lương lịch trình
    private double cost;          // Chi phí liên quan
    private String description;   // Mô tả chuyến đi


}