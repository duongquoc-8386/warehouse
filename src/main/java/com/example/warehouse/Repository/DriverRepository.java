package com.example.warehouse.Repository;

import com.example.warehouse.Entity.Driver;
import com.example.warehouse.Enum.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByEmployeeCode(String employeeCode);
    List<Driver> findByStatus(DriverStatus status);
    Optional<Driver> findByEmployeeCode(String code);
}
