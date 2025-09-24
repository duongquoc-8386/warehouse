    package com.example.warehouse.Service;

    import com.example.warehouse.Dto.Response.*;
    import com.example.warehouse.Entity.*;
    import com.example.warehouse.Repository.*;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    import java.math.BigDecimal;
    import java.time.YearMonth;
    import java.time.format.DateTimeFormatter;
    import java.util.*;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    public class WarehouseReportService {

        private final ProductRepository productRepository;
        private final InventoryTransactionRepository inventoryTransactionRepository;
        private final ExpenseRepository expenseRepository;
        private final ExpenseTypeRepository expenseTypeRepository;
        private final TripRepository tripRepository;
        private final DriverRepository driverRepository;
        private final TripSalaryRepository tripSalaryRepository;
        private final SalaryConfigRepository salaryConfigRepository;
        private final AdvancePaymentRepository advancePaymentRepository;

        // ================= TỒN KHO =================
        public List<StockReportResponse> getStockReport() {
            List<Product> products = productRepository.findAll();

            List<StockReportResponse> report = new ArrayList<>();
            for (Product p : products) {
                int totalImported = inventoryTransactionRepository.findByProductId(p.getId())
                        .stream().filter(t -> t.getType().name().equals("IMPORT"))
                        .mapToInt(t -> t.getQuantity()).sum();
                int totalExported = inventoryTransactionRepository.findByProductId(p.getId())
                        .stream().filter(t -> t.getType().name().equals("EXPORT"))
                        .mapToInt(t -> t.getQuantity()).sum();

                report.add(StockReportResponse.builder()
                        .productId(p.getId())
                        .productName(p.getName())
                        .currentStock(Optional.ofNullable(p.getCurrentStock()).orElse(0))
                        .totalImported(totalImported)
                        .totalExported(totalExported)
                        .build());
            }
            return report;
        }

        // ================= CHI PHÍ =================
        public List<ExpenseReportResponse> getExpenseReport() {
            List<ExpenseType> types = expenseTypeRepository.findAll();
            List<Expense> expenses = expenseRepository.findAll();

            List<ExpenseReportResponse> report = new ArrayList<>();
            for (ExpenseType type : types) {
                double total = expenses.stream()
                        .filter(e -> e.getExpenseType() != null && e.getExpenseType().getId().equals(type.getId()))
                        .mapToDouble(e -> Optional.ofNullable(e.getAmount()).orElse(0.0))
                        .sum();
                long count = expenses.stream()
                        .filter(e -> e.getExpenseType() != null && e.getExpenseType().getId().equals(type.getId()))
                        .count();

                report.add(ExpenseReportResponse.builder()
                        .expenseTypeId(type.getId())
                        .expenseTypeName(type.getName())
                        .totalAmount(total)
                        .count(count)
                        .build());
            }
            return report;
        }

        // ================= CHUYẾN ĐI =================
        public List<TripReportResponse> getTripReport() {
            List<Driver> drivers = driverRepository.findAll();
            List<Trip> trips = tripRepository.findAll();

            List<TripReportResponse> report = new ArrayList<>();
            for (Driver d : drivers) {
                List<Trip> driverTrips = trips.stream()
                        .filter(t -> t.getDriver() != null && t.getDriver().getId().equals(d.getId()))
                        .collect(Collectors.toList());
                double totalCost = driverTrips.stream()
                        .mapToDouble(t -> Optional.ofNullable(t.getCost()).orElse(0.0))
                        .sum();

                report.add(TripReportResponse.builder()
                        .driverId(d.getId())
                        .driverName(d.getFullName())
                        .tripCount(driverTrips.size())
                        .totalTripCost(totalCost)
                        .build());
            }
            return report;
        }

        // ================= LƯƠNG =================
        public List<SalaryReportResponse> getSalaryReport(String month) {
            List<Driver> drivers = driverRepository.findAll();
            List<SalaryReportResponse> results = new ArrayList<>();

            for (Driver d : drivers) {
                SalaryReportResponse dto = new SalaryReportResponse();
                dto.setDriverId(d.getId());
                dto.setDriverName(d.getFullName());

                // Lấy lương cơ bản và phụ cấp từ entity Driver
                Double baseSalary = d.getBaseSalary() != null ? d.getBaseSalary() : 0.0;
                Double allowance = d.getAllowance() != null ? d.getAllowance() : 0.0;

                // Lấy lương chuyến đi trong tháng
                Double tripSalary = tripRepository.sumCostByDriverAndMonth(d.getId(), month);
                if (tripSalary == null) tripSalary = 0.0;

                // Lấy tổng ứng lương trong tháng
                Double advancePayment = advancePaymentRepository.sumByDriverAndMonth(d.getId(), month);
                if (advancePayment == null) advancePayment = 0.0;

                // Gán giá trị vào DTO
                dto.setBaseSalary(baseSalary + allowance);
                dto.setTripSalary(tripSalary);
                dto.setAdvancePayment(advancePayment);

                // Tổng lương = baseSalary + allowance + tripSalary - advancePayment
                dto.setTotalSalary(baseSalary + allowance + tripSalary - advancePayment);

                results.add(dto);
            }

            return results;
        }

    }

// ** baseSalary = 12,000,000 mức lương hàng tháng
//
//allowance = 3,000,000 phụ cấp
//
//tripSalary = 900,000 tiền sau các chuyến xe chạy
//
//advancePayment = 500,000 tiền ứng
//
// totalSalary = 12,000,000 + 3,000,000 + 900,000 - 500,000 = 15,400,000**/