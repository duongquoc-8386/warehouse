package com.example.warehouse.Repository;

import com.example.warehouse.Entity.ScheduleSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleSalaryRepository extends JpaRepository<ScheduleSalary, Long> {

    @Query("SELECT SUM(s.amount) FROM ScheduleSalary s WHERE s.driver.id = :driverId AND s.createdAt BETWEEN :from AND :to")
    BigDecimal sumAmountByDriverBetween(@Param("driverId") Long driverId,
                                        @Param("from") LocalDate from,
                                        @Param("to") LocalDate to);

    List<ScheduleSalary> findByDriverId(Long driverId);
}
