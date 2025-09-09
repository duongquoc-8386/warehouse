package com.example.warehouse.Repository;


import com.example.warehouse.Entity.TonKho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TonKhoRepository extends JpaRepository<TonKho, Long> {
    List<TonKho> findByTenHangHoaContainingIgnoreCase(String keyword);
}
