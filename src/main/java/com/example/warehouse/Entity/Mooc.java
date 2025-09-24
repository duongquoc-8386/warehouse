package com.example.warehouse.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "mooc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mooc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String licensePlate; // biển số rơ-moóc

    private String type; // loại (container, bồn, thùng kín, ...)

    private Double weight; // trọng lượng

    private String status; // trạng thái: đang hoạt động / bảo dưỡng
}
