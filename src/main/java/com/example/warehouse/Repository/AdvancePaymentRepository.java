package com.example.warehouse.Repository;

import com.example.warehouse.Entity.AdvancePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdvancePaymentRepository extends JpaRepository<AdvancePayment, Long> {
    @Query("SELECT COALESCE(SUM(a.amount), 0) " +
            "FROM AdvancePayment a " +
            "WHERE a.driver.id = :driverId " +
            "AND FUNCTION('DATE_FORMAT', a.paymentDate, '%Y-%m') = :month")
    Double sumByDriverAndMonth(@Param("driverId") Long driverId,
                               @Param("month") String month);
}