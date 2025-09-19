package com.example.warehouse.Repository;

import com.example.warehouse.Entity.SalaryConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaryConfigRepository extends JpaRepository<SalaryConfig, Long> {

    // Thêm phương thức tìm theo driverId và month
    Optional<SalaryConfig> findByDriverIdAndMonth(Long driverId, String month);
}
