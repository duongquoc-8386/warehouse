package com.example.warehouse.Repository;

import com.example.warehouse.Entity.Product;
import com.example.warehouse.Enum.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCode(String code);
    List<Product> findByNameContainingIgnoreCase(String name);
    boolean existsByCode(String code);
}
