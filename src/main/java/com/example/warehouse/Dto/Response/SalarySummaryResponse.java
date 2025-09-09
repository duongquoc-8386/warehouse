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
public class SalarySummaryResponse {
    private String month;
    private Long driverId;
    private String driverName;

    private BigDecimal baseSalary;
    private BigDecimal scheduleSalary;
    private BigDecimal expense;
    private BigDecimal totalExpense;
    private BigDecimal advance;

    private BigDecimal finalSalary;
}
