package com.example.warehouse.Dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportFilterRequest {
    private Long truckId;           // ID xe tải
    private LocalDate startDate;    // Ngày bắt đầu lọc
    private LocalDate endDate;      // Ngày kết thúc lọc
}
