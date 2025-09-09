package com.example.warehouse.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingScheduleResponse {
    private Long id;
    private String title;
    private String startLocation;
    private String endLocation;
    private String status;

}
