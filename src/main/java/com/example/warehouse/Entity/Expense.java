package com.example.warehouse.Entity;

import com.example.warehouse.Enum.ExpenseStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Số tiền chi phí
    @NotNull(message = "Amount is required")
    @Column(nullable = false)
    private Double amount;

    // Loại chi phí: Nhiên liệu, Bảo trì, Sửa chữa, ...
    @ManyToOne
    @JoinColumn(name = "expense_type_id", nullable = false)
    private ExpenseType type;

    // Mô tả chi phí
    private String description;

    // Trạng thái phê duyệt
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "truck_id")
    private Truck truck;

    // ---- Tự động mặc định khi tạo mới ----
    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = ExpenseStatus.PENDING;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
