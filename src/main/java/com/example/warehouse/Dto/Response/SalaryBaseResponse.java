package com.example.warehouse.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryBaseResponse {
    private Long driverId;
    private String driverName;

    private BigDecimal baseSalary;       // Lương cơ bản
    private BigDecimal scheduleSalary;   // Lương từ lịch trình
    private BigDecimal totalExpense;     // Tổng chi phí
    private BigDecimal advance;          // Tiền ứng

    private BigDecimal finalSalary;      // Lương cuối cùng sau tính toán
}
