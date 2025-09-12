package com.example.warehouse.Repository;

import com.example.warehouse.Entity.SalaryConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

public interface SalaryConfigRepository extends JpaRepository<SalaryConfig, Long> {

    @Query("SELECT SUM(s.baseSalary) FROM SalaryConfig s WHERE s.driver.id = :driverId AND s.month BETWEEN :fromMonth AND :toMonth")
    BigDecimal sumBaseSalaryForDriverBetweenMonths(@Param("driverId") Long driverId,
                                                   @Param("fromMonth") String fromMonth,
                                                   @Param("toMonth") String toMonth);

    @Query("SELECT SUM(s.advance) FROM SalaryConfig s WHERE s.driver.id = :driverId AND s.month BETWEEN :fromMonth AND :toMonth")
    BigDecimal sumAdvanceForDriverBetweenMonths(@Param("driverId") Long driverId,
                                                @Param("fromMonth") String fromMonth,
                                                @Param("toMonth") String toMonth);
}
