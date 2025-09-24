package com.example.warehouse.Repository;


import com.example.warehouse.Entity.Trip;
import com.example.warehouse.Enum.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByStatus(TripStatus status);
    List<Trip> findByDriverId(Long driverId);
    List<Trip> findByTruckId(Long truckId);
    @Query("SELECT SUM(t.cost) " +
            "FROM Trip t " +
            "WHERE t.driver.id = :driverId " +
            "AND FUNCTION('DATE_FORMAT', t.startTime, '%Y-%m') = :month")
    Double sumCostByDriverAndMonth(@Param("driverId") Long driverId,
                                   @Param("month") String month);
}
