package com.example.warehouse.Repository;



import com.example.warehouse.Entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findByRouteCode(String routeCode);
    boolean existsByRouteCode(String routeCode);
}
