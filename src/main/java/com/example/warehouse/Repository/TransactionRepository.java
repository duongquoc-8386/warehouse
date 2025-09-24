package com.example.warehouse.Repository;



import com.example.warehouse.Entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<InventoryTransaction, Long> {
}
