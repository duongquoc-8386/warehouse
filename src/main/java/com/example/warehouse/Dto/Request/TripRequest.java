package com.example.warehouse.Dto.Request;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripRequest {
    private String title;          // Tiêu đề chuyến đi (VD: Hà Nội - Thái Bình)
    private Double cost;           // Chi phí
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;    // Mô tả chuyến đi
    private String status;         // PENDING / APPROVED / REJECTED / SUBMITTED

    private Long driverId;         // ID tài xế
    private Long truckId;          // ID xe tải
    private Long routeId;          // ID tuyến đường
}
