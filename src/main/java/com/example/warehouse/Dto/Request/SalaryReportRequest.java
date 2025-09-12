package com.example.warehouse.Dto.Request;



import lombok.Data;

@Data
public class SalaryReportRequest {
    private Long driverId;
    private String fromDate; // yyyy-MM-dd
    private String toDate;   // yyyy-MM-dd
}
