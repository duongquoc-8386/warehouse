package com.example.warehouse.Dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryRequest {
    private Long driverId;         // ID tài xế
    private YearMonth month;       // Tháng tính lương
}
