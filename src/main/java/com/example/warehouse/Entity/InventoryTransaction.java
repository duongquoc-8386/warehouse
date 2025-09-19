package com.example.warehouse.Entity;

import com.example.warehouse.Enum.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // IMPORT, EXPORT, ADJUSTMENT, RETURN...

    @Column(nullable = false)
    private Integer quantity;

    private String referenceCode; // Mã phiếu nhập / xuất / trả hàng...

    private String createdBy; // Người thực hiện

    private String note;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
