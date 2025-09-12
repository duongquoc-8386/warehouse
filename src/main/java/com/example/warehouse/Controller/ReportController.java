package com.example.warehouse.Controller;

import com.example.warehouse.Dto.Response.DriverReportResponse;
import com.example.warehouse.Dto.Response.*;
import com.example.warehouse.Service.ReportService;
import org.springframework.web.bind.annotation.*;



import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Báo cáo Lương
    @GetMapping("/salary/{driverId}")
    public DriverReportResponse getSalaryReport(@PathVariable Long driverId) {
        return reportService.getSalaryReportAllByDriver(driverId);
    }

    // Báo cáo Chi phí
    @GetMapping("/costs/truck/{truckId}")
    public VehicleCostResponse getVehicleCost(@PathVariable Long truckId) {
        return reportService.getVehicleCostReport(truckId);
    }

    @GetMapping("/costs/all")
    public VehicleCostSummaryResponse getVehicleCostAll() {
        return reportService.getVehicleCostAll();
    }

    // Báo cáo Lịch trình
    @GetMapping("/schedules/truck/{truckId}")
    public List<ScheduleDetailResponse> getSchedulesForTruck(@PathVariable Long truckId) {
        return reportService.getScheduleDetailsAllByTruck(truckId);
    }

    @GetMapping("/schedules")
    public AllScheduleDetailResponse getAllSchedules() {
        return reportService.getScheduleDetailsAll();
    }
}
