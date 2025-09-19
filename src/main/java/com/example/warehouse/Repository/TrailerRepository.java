package com.example.warehouse.Repository;

import com.example.warehouse.Entity.Trailer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TrailerRepository extends JpaRepository<Trailer, Long> {

    Optional<Trailer> findByCode(String code);
}
