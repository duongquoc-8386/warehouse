package com.example.warehouse.Repository;

import com.example.warehouse.Entity.Expense;
import com.example.warehouse.Enum.ExpenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByStatus(ExpenseStatus status);
    @Query("SELECT e FROM Expense e JOIN Schedule s ON s.truck.id = e.id WHERE s.driver.id = :driverId AND FUNCTION('DATE_FORMAT', e.createdAt, '%Y-%m') = :month")
    List<Expense> findByDriverAndMonth(@Param("driverId") Long driverId, @Param("month") String month);
}
