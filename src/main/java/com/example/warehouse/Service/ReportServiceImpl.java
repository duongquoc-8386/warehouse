package com.example.warehouse.Service;

import com.example.warehouse.Dto.Request.SalaryConfigRequest;
import com.example.warehouse.Dto.Request.SalaryRequest;
import com.example.warehouse.Dto.Request.ReportFilterRequest;
import com.example.warehouse.Dto.Response.ExpenseResponse;
import com.example.warehouse.Dto.Response.SalaryDetailResponse;
import com.example.warehouse.Dto.Response.SalarySummaryResponse;
import com.example.warehouse.Dto.Response.TruckExpenseSummaryResponse;
import com.example.warehouse.Entity.Driver;
import com.example.warehouse.Entity.Expense;
import com.example.warehouse.Entity.SalaryConfig;
import com.example.warehouse.Repository.DriverRepository;
import com.example.warehouse.Repository.ExpenseRepository;
import com.example.warehouse.Repository.SalaryConfigRepository;
import com.example.warehouse.Repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final DriverRepository driverRepository;
    private final SalaryConfigRepository salaryConfigRepository;
    private final ScheduleRepository scheduleRepository;
    private final ExpenseRepository expenseRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public List<SalarySummaryResponse> getSalarySummary(SalaryRequest request) {
        String monthString = request.getMonth().format(FORMATTER); // Convert YearMonth -> "yyyy-MM"

        List<Driver> drivers = driverRepository.findAll();

        return drivers.stream().map(driver -> {
            // 1. Lấy lương cơ bản + tiền ứng
            SalaryConfig config = salaryConfigRepository.findByDriverIdAndMonth(driver.getId(), monthString)
                    .orElse(null);

            BigDecimal baseSalary = config != null ? config.getBaseSalary() : BigDecimal.ZERO;
            BigDecimal advance = config != null ? config.getAdvance() : BigDecimal.ZERO;

            // 2. Tổng lương từ lịch trình
            BigDecimal totalScheduleSalary = scheduleRepository.findByDriverAndMonth(driver.getId(), monthString)
                    .stream()
                    .map(schedule -> schedule.getScheduleSalary() != null ? schedule.getScheduleSalary() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 3. Tổng chi phí xe
            BigDecimal totalTruckExpense = expenseRepository.findByDriverAndMonth(driver.getId(), monthString)
                    .stream()
                    .map(expense -> expense.getAmount() != null ? BigDecimal.valueOf(expense.getAmount()) : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 4. Công thức lương
            BigDecimal finalSalary = baseSalary
                    .add(totalScheduleSalary)
                    .add(totalTruckExpense)
                    .subtract(advance);

            // 5. Build response
            return SalarySummaryResponse.builder()
                    .driverId(driver.getId())
                    .driverName(driver.getFullName())
                    .baseSalary(baseSalary)
                    .scheduleSalary(totalScheduleSalary)
                    .expense(totalTruckExpense)
                    .advance(advance)
                    .finalSalary(finalSalary)
                    .build();

        }).collect(Collectors.toList());
    }


    @Override
    public List<SalaryDetailResponse> getSalaryDetail(SalaryRequest request) {
        // TODO: Viết logic chi tiết sau
        return List.of();
    }

    @Override
    public void updateSalaryConfig(SalaryConfigRequest request) {
        // TODO: Lưu cấu hình lương vào DB
        // ví dụ:
        // SalaryConfig config = new SalaryConfig();
        // config.setDriverId(request.getDriverId());
        // config.setMonth(request.getMonth());
        // config.setBaseSalary(request.getBaseSalary());
        // config.setAdvance(request.getAdvance());
        // salaryConfigRepository.save(config);
    }

    @Override
    public List<ExpenseResponse> getTruckExpenses(ReportFilterRequest request) {
        // TODO: Viết logic chi phí theo từng xe
        return List.of();
    }

    @Override
    public List<TruckExpenseSummaryResponse> getAllTruckExpenses(ReportFilterRequest request) {
        // TODO: Viết logic tổng hợp chi phí tất cả xe
        return List.of();
    }


}
