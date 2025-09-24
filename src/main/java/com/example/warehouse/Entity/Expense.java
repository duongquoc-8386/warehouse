package com.example.warehouse.Entity;

import com.example.warehouse.Enum.ExpenseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @JoinColumn(name = "expense_type_id")
    private ExpenseType expenseType;

    // Mô tả chi phí
    private String description;

    // Trạng thái phê duyệt
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "truck_id", referencedColumnName = "id")

    private Truck truck;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public ExpenseType getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(ExpenseType expenseType) {
        this.expenseType = expenseType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExpenseStatus getStatus() {
        return status;
    }

    public void setStatus(ExpenseStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }

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
