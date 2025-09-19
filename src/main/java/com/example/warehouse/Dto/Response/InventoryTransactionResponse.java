package com.example.warehouse.Dto.Response;

import com.example.warehouse.Enum.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryTransactionResponse {
    private Long id;
    private String productName;
    private TransactionType type;
    private int quantity;
    private String createdBy;
    private LocalDateTime createdAt;
    private String note;
}
