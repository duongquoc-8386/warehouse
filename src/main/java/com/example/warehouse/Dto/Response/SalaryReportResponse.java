package com.example.warehouse.Dto.Response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SalaryReportResponse {
    private Long driverId;
    private String driverName;

    private Double basicSalary;
    private Double performanceSalary;
    private Double advancePayment;
    private Double totalSalary;

    private List<DriverSalaryDetail> details;
}
