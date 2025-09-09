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
public class TruckExpenseSummaryResponse {
    private Long truckId;
    private String truckPlate;        // Biển số xe
    private BigDecimal totalExpense;  // Tổng chi phí của xe trong khoảng thời gian
}
