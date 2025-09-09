package com.example.warehouse.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {

    private LocalDate date;           // Ngày phát sinh chi phí
    private String expenseType;       // Loại chi phí (nhiên liệu, bảo dưỡng, ...)
    private String description;       // Mô tả chi phí
    private BigDecimal amount;        // Số tiền chi phí
    private String truckPlate;        // Biển số xe
}
