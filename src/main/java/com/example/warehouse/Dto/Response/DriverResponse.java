package com.example.warehouse.Dto.Response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DriverResponse {
    private Long id;
    private String employeeCode;
    private String fullName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String note;
    private String status;
    private Double basicSalary;
    private Double advancePayment;
}

