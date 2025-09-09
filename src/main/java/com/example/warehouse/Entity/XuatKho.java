package com.example.warehouse.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "xuat_kho")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class XuatKho {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenHangHoa;

    private String loaiHang;
    private Integer  soLuong;
    private String dichDen;
    private String nguoiXuat;
    private String destination;
    private LocalDateTime thoiGianXuat = LocalDateTime.now();
}


