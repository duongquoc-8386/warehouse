package com.example.warehouse.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseReportRepository {
    @Query("SELECT new com.example.warehouse.Dto.ExpenseReportDTO(e.truck.id, e.truck.name, SUM(e.amount), :fromDate, :toDate) " +
            "FROM Expense e " +
            "WHERE e.createdAt BETWEEN :fromDate AND :toDate " +
            "GROUP BY e.truck.id, e.truck.name")
    List<ExpenseReportRepository> sumExpenseByTruckBetween(@Param("fromDate") LocalDateTime fromDate,
                                                           @Param("toDate") LocalDateTime toDate);

}
