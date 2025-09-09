package com.example.warehouse.Controller;

import com.example.warehouse.Dto.Request.*;
import com.example.warehouse.Dto.Response.*;
import com.example.warehouse.Service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/salary/summary")
    public List<SalarySummaryResponse> getSalarySummary(@RequestBody SalaryRequest request) {
        return reportService.getSalarySummary(request);
    }

    @PostMapping("/salary/detail")
    public List<SalaryDetailResponse> getSalaryDetail(@RequestBody SalaryRequest request) {
        return reportService.getSalaryDetail(request);
    }

    @PostMapping("/salary/config")
    public void updateSalaryConfig(@RequestBody SalaryConfigRequest request) {
        reportService.updateSalaryConfig(request);
    }

    @PostMapping("/expenses/truck")
    public List<ExpenseResponse> getTruckExpenses(@RequestBody ReportFilterRequest request) {
        return reportService.getTruckExpenses(request);
    }

    @PostMapping("/expenses/trucks/summary")
    public List<TruckExpenseSummaryResponse> getAllTruckExpenses(@RequestBody ReportFilterRequest request) {
        return reportService.getAllTruckExpenses(request);
    }
}
