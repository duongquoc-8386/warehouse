package com.example.warehouse.Repository;

import com.example.warehouse.Entity.Expense;
import com.example.warehouse.Enum.ExpenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT COALESCE(SUM(e.amount), 0) " +
            "FROM Expense e " +
            "WHERE e.createdAt BETWEEN :from AND :to")
    BigDecimal sumExpenseBetween(@Param("from") LocalDateTime from,
                                 @Param("to") LocalDateTime to);

    @Query("SELECT COALESCE(SUM(e.amount), 0) " +
            "FROM Expense e " +
            "WHERE e.expenseType.name = :type " +  // dùng field name của ExpenseType
            "AND e.createdAt BETWEEN :from AND :to")
    BigDecimal sumExpenseByTypeBetween(@Param("type") String type,
                                       @Param("from") LocalDateTime from,
                                       @Param("to") LocalDateTime to);

    List<Expense> findByTruckIdAndStatus(Long truckId, ExpenseStatus status);
}
