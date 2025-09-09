package com.example.warehouse.Repository;

import com.example.warehouse.Entity.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Long> {
}
