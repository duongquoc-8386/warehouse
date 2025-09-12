package com.example.warehouse.Dto.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverReportResponse {
    private Long driverId;
    private String driverName;

    private double basicSalary;       // Lương cơ bản
    private double performanceSalary; // Lương hiệu suất (lịch trình + chi phí)
    private double advancePayment;    // Tiền ứng
    private double totalSalary;       // Lương cuối cùng
    private List<DriverSalaryDetail> details; // Chi tiết từng chuyến hoặc chi phí

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public double getPerformanceSalary() {
        return performanceSalary;
    }

    public void setPerformanceSalary(double performanceSalary) {
        this.performanceSalary = performanceSalary;
    }

    public double getAdvancePayment() {
        return advancePayment;
    }

    public void setAdvancePayment(double advancePayment) {
        this.advancePayment = advancePayment;
    }

    public double getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(double totalSalary) {
        this.totalSalary = totalSalary;
    }

    public List<DriverSalaryDetail> getDetails() {
        return details;
    }

    public void setDetails(List<DriverSalaryDetail> details) {
        this.details = details;
    }
}
