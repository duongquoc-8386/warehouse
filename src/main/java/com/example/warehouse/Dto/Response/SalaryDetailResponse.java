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
public class SalaryDetailResponse {

    private LocalDate date;           // Ngày chuyến đi
    private String journeyCode;       // Mã lịch trình
    private String description;       // Mô tả công việc hoặc chi phí

    private BigDecimal scheduleSalary; // Lương từ lịch trình
    private BigDecimal expense;        // Chi phí phát sinh
}
