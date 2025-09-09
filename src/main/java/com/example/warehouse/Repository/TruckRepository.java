package com.example.warehouse.Repository;

import com.example.warehouse.Entity.Truck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TruckRepository extends JpaRepository<Truck, Long> {
}

