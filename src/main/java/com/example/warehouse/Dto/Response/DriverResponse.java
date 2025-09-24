package com.example.warehouse.Dto.Response;

import com.example.warehouse.Entity.Driver;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverResponse {
    private Long id;
    private String fullName;
    private String employeeCode;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String status;

    public static DriverResponse fromEntity(Driver driver) {
        if (driver == null) return null;
        return DriverResponse.builder()
                .id(driver.getId())
                .fullName(driver.getFullName() != null ? driver.getFullName() : "Chưa cập nhật")
                .employeeCode(driver.getEmployeeCode() != null ? driver.getEmployeeCode() : "")
                .phoneNumber(driver.getPhoneNumber() != null ? driver.getPhoneNumber() : "")
                .dateOfBirth(driver.getDateOfBirth())
                .status(driver.getStatus() != null ? driver.getStatus().toString() : "Chưa cập nhật")
                .build();
    }
}
