package com.example.warehouse.Repository;

import com.example.warehouse.Entity.Expense;
import com.example.warehouse.Enum.ExpenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.createdAt BETWEEN :from AND :to")
    BigDecimal sumExpenseBetween(@Param("from") LocalDate from,
                                 @Param("to") LocalDate to);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.type = :type AND e.createdAt BETWEEN :from AND :to")
    BigDecimal sumExpenseByTypeBetween(@Param("type") String type,
                                       @Param("from") LocalDate from,
                                       @Param("to") LocalDate to);
    List<Expense> findByTruckIdAndStatus(Long truckId, ExpenseStatus status);
}
