package com.example.warehouse.Repository;



import com.example.warehouse.Entity.Inventory;
import com.example.warehouse.Entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByVariant(ProductVariant variant);
}
