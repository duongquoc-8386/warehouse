package com.example.warehouse.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor

@Builder
public class DriverSalaryDetail {
    private Long driverId;
    private String description;

    // lương theo chuyến (trip cost)
    private double tripSalary;

    // lương cơ bản (cộng thêm nếu có)
    private double baseSalary;

    // giữ lại routeSalary để tương thích với chỗ .mapToDouble(DriverSalaryDetail::getRouteSalary)
    private double routeSalary;
    private Double advancePayment;  // thêm field này
    private Double totalSalary;
    public DriverSalaryDetail() {}



}
