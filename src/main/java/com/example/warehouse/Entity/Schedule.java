package com.example.warehouse.Entity;

import com.example.warehouse.Enum.ScheduleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Chi phí (nhưng thực tế sẽ lấy từ scheduleSalary)
    private Double cost;

    private String title;
    private LocalDate date;
    private String description;
    private String startLocation;
    private String endLocation;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "truck_id", nullable = false)
    private Truck truck;

    // Lương hiệu suất cho chuyến này
    @Column(nullable = false)
    private BigDecimal scheduleSalary = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();

    private String proofDocumentPath;

    @Transient
    private String costFormatted;

    //  Luôn trả cost = scheduleSalary
    public Double getCost() {
        if (this.scheduleSalary != null) {
            return this.scheduleSalary.doubleValue();
        }
        return this.cost;
    }

    //  Format cost thành tiền VNĐ
    public String getCostFormatted() {
        if (this.scheduleSalary != null) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            return formatter.format(this.scheduleSalary);
        }
        return costFormatted;
    }
}
