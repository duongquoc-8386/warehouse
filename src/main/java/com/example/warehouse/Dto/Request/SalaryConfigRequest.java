package com.example.warehouse.Dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryConfigRequest {
    private Long driverId;           // ID tài xế
    private BigDecimal baseSalary;   // Lương cơ bản
    private BigDecimal advance;      // Tiền ứng cố định/tháng
}
