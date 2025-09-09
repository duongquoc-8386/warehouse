package com.example.warehouse.Repository;

import com.example.warehouse.Entity.Product;
import com.example.warehouse.Enum.LoaiHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByTenHangHoaContainingIgnoreCase(String keyword);
    List<Product> findByLoaiHang(LoaiHang loaiHang);
    Product findByTenHangHoa(String tenHangHoa);
}
