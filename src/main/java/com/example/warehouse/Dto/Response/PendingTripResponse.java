package com.example.warehouse.Dto.Response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingTripResponse {
    private Long id;            // ID chuyến đi
    private String title;       // Tiêu đề chuyến đi (biển số xe chẳng hạn)
    private String routeName;   // Tên tuyến đường
    private String status;      // Trạng thái (PENDING)
    private String startTime;   // Thời gian bắt đầu
    private String endTime;     // Thời gian kết thúc

    // Constructor khớp với Service hiện tại
    public PendingTripResponse(Long id, String title, LocalDateTime startTime, LocalDateTime endTime, String routeName) {
        this.id = id;
        this.title = title;
        this.startTime = startTime != null ? startTime.toString() : null;
        this.endTime = endTime != null ? endTime.toString() : null;
        this.routeName = routeName;
        this.status = "PENDING";
    }
}
