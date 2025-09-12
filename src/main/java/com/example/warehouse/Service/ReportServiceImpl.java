package com.example.warehouse.Service;

import com.example.warehouse.Dto.Response.*;
import com.example.warehouse.Entity.Driver;
import com.example.warehouse.Entity.Expense;
import com.example.warehouse.Entity.Schedule;
import com.example.warehouse.Entity.ScheduleSalary;
import com.example.warehouse.Entity.Truck;
import com.example.warehouse.Enum.ExpenseStatus;
import com.example.warehouse.Repository.DriverRepository;
import com.example.warehouse.Repository.ExpenseRepository;
import com.example.warehouse.Repository.ScheduleRepository;
import com.example.warehouse.Repository.ScheduleSalaryRepository;
import com.example.warehouse.Repository.TruckRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleSalaryRepository scheduleSalaryRepository;
    private final ExpenseRepository expenseRepository;

    public ReportServiceImpl(DriverRepository driverRepository,
                             TruckRepository truckRepository,
                             ScheduleRepository scheduleRepository,
                             ScheduleSalaryRepository scheduleSalaryRepository,
                             ExpenseRepository expenseRepository) {
        this.driverRepository = driverRepository;
        this.truckRepository = truckRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleSalaryRepository = scheduleSalaryRepository;
        this.expenseRepository = expenseRepository;
    }

    // ---------- Báo cáo Lương ----------
    @Override
    public DriverReportResponse getSalaryReportAllByDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        DriverReportResponse response = new DriverReportResponse();
        response.setDriverId(driver.getId());
        response.setDriverName(driver.getFullName());
        response.setBasicSalary(5000000.0);
        response.setAdvancePayment(1000000.0);

        // Tính performanceSalary từ scheduleSalary
        List<Schedule> schedules = scheduleRepository.findByDriverId(driverId);

        List<DriverSalaryDetail> details = schedules.stream().map(s -> {
            DriverSalaryDetail detail = new DriverSalaryDetail();
            detail.setScheduleId(s.getId());
            detail.setRouteSalary(s.getScheduleSalary().doubleValue()); // lương lịch trình
            detail.setCost(0.0); // placeholder nếu chưa có chi phí, hoặc tính từ Expense
            detail.setDescription(s.getDescription());
            return detail;
        }).collect(Collectors.toList());

        response.setDetails(details);

// Tính tổng performanceSalary
        double performanceSalary = details.stream().mapToDouble(DriverSalaryDetail::getRouteSalary).sum();
        response.setPerformanceSalary(performanceSalary);

// Tính tổng lương
        double totalSalary = response.getBasicSalary() + performanceSalary - response.getAdvancePayment();
        response.setTotalSalary(totalSalary);





        return response;
    }

    // ---------- Báo cáo Chi phí ----------
    @Override
    public VehicleCostResponse getVehicleCostReport(Long truckId) {
        Truck truck = truckRepository.findById(truckId)
                .orElseThrow(() -> new RuntimeException("Truck not found"));

        List<Schedule> schedules = scheduleRepository.findByTruckId(truckId);
        List<Expense> expenses = expenseRepository.findByTruckIdAndStatus(truckId, ExpenseStatus.APPROVED);

        List<VehicleCostDetail> details = new ArrayList<>();
        double totalCost = 0;

        for (Schedule s : schedules) {
            VehicleCostDetail detail = new VehicleCostDetail();
            detail.setScheduleId(s.getId());
            detail.setCost(s.getScheduleSalary() != null ? s.getScheduleSalary().doubleValue() : 0.0);
            detail.setDescription(s.getDescription());
            totalCost += detail.getCost();
            details.add(detail);
        }

        for (Expense e : expenses) {
            VehicleCostDetail detail = new VehicleCostDetail();
            detail.setScheduleId(null);
            detail.setCost(e.getAmount() != null ? e.getAmount() : 0.0);
            detail.setDescription(e.getType() + ": " + e.getDescription());
            totalCost += detail.getCost();
            details.add(detail);
        }

        VehicleCostResponse response = new VehicleCostResponse();
        response.setTruckId(truck.getId());
        response.setTruckCode(truck.getLicensePlate());
        response.setTotalCost(totalCost);
        response.setDetails(details);

        return response;
    }

    @Override
    public VehicleCostSummaryResponse getVehicleCostAll() {
        List<Truck> trucks = truckRepository.findAll();
        List<VehicleCostResponse> vehicleCosts = new ArrayList<>();
        double totalCostAllTrucks = 0;

        for (Truck truck : trucks) {
            VehicleCostResponse vcr = getVehicleCostReport(truck.getId());
            vehicleCosts.add(vcr);
            totalCostAllTrucks += vcr.getTotalCost();
        }

        VehicleCostSummaryResponse summary = new VehicleCostSummaryResponse();
        summary.setVehicleCosts(vehicleCosts);
        summary.setTotalCostAllTrucks(totalCostAllTrucks);
        return summary;
    }

    // ---------- Báo cáo Lịch trình ----------
    @Override
    public List<ScheduleDetailResponse> getScheduleDetailsAllByTruck(Long truckId) {
        List<Schedule> schedules = scheduleRepository.findByTruckId(truckId);
        List<ScheduleDetailResponse> responses = new ArrayList<>();

        for (Schedule s : schedules) {
            ScheduleDetailResponse response = new ScheduleDetailResponse();
            response.setScheduleId(s.getId());
            response.setTruckCode(s.getTruck() != null ? s.getTruck().getLicensePlate() : "");
            response.setDriverName(s.getDriver() != null ? s.getDriver().getFullName() : "");
            response.setDescription(s.getDescription());
            response.setCost(s.getScheduleSalary() != null ? s.getScheduleSalary().doubleValue() : 0.0);
            response.setDocumentUrl(s.getProofDocumentPath() != null ? s.getProofDocumentPath() : "");
            responses.add(response);
        }
        return responses;
    }

    @Override
    public AllScheduleDetailResponse getScheduleDetailsAll() {
        List<Schedule> schedules = scheduleRepository.findAll();
        List<ScheduleDetailResponse> responses = new ArrayList<>();
        double totalCostAllTrucks = 0;

        for (Schedule s : schedules) {
            ScheduleDetailResponse response = new ScheduleDetailResponse();
            response.setScheduleId(s.getId());
            response.setTruckCode(s.getTruck() != null ? s.getTruck().getLicensePlate() : "");
            response.setDriverName(s.getDriver() != null ? s.getDriver().getFullName() : "");
            response.setDescription(s.getDescription());
            response.setCost(s.getScheduleSalary() != null ? s.getScheduleSalary().doubleValue() : 0.0);
            response.setDocumentUrl(s.getProofDocumentPath() != null ? s.getProofDocumentPath() : "");
            totalCostAllTrucks += response.getCost();
            responses.add(response);
        }

        AllScheduleDetailResponse summary = new AllScheduleDetailResponse();
        summary.setSchedules(responses);
        summary.setTotalCostAllTrucks(totalCostAllTrucks);
        return summary;
    }
}
