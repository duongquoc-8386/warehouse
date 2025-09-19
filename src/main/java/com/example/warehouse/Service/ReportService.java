package com.example.warehouse.Service;

import com.example.warehouse.Dto.Response.*;
import java.util.List;

public interface ReportService {

    // Báo cáo lương
    DriverReportResponse getSalaryReportAllByDriver(Long driverId);

    // Báo cáo chi phí
    VehicleCostResponse getVehicleCostReport(Long truckId);
    VehicleCostSummaryResponse getVehicleCostAll();

    // Báo cáo lịch trình
    List<TripDetailResponse> getScheduleDetailsAllByTruck(Long truckId);
    AllScheduleDetailResponse getScheduleDetailsAll();
}
