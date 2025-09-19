package com.example.warehouse.Repository;

import com.example.warehouse.Entity.TripSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface TripSalaryRepository extends JpaRepository<TripSalary, Long> {
    List<TripSalary> findByDriverIdAndMonth(Long driverId, String month);

}