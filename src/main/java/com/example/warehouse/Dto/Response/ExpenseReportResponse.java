package com.example.warehouse.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseReportResponse  {
    private Long expenseTypeId;
    private String expenseTypeName;
    private double totalAmount;
    private long count;
}
