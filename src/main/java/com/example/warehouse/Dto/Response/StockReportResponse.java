package com.example.warehouse.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



// Báo cáo tồn kho
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReportResponse {
    private Long productId;
    private String productName;
    private int currentStock;
    private int totalImported;
    private int totalExported;
}