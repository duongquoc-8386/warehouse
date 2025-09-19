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
                        .filter(e -> e.getType() != null && e.getType().getId().equals(type.getId()))
                        .mapToDouble(e -> Optional.ofNullable(e.getAmount()).orElse(0.0))
                        .sum();
                long count = expenses.stream()
                        .filter(e -> e.getType() != null && e.getType().getId().equals(type.getId()))
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

            YearMonth yearMonth = (month == null || month.isEmpty())
                    ? YearMonth.now()
                    : YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));


            String monthStr = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            List<Driver> drivers = driverRepository.findAll();
            List<SalaryReportResponse> report = new ArrayList<>();

            for (Driver d : drivers) {
                // Base salary
                BigDecimal baseSalary = salaryConfigRepository
                        .findByDriverIdAndMonth(d.getId(), monthStr)
                        .map(SalaryConfig::getBaseSalary)
                        .orElse(BigDecimal.ZERO);

                // Advance
                BigDecimal advance = salaryConfigRepository
                        .findByDriverIdAndMonth(d.getId(), monthStr)
                        .map(SalaryConfig::getAdvance)
                        .orElse(BigDecimal.ZERO);

                // Trip salary theo tháng
                BigDecimal tripSalary = tripSalaryRepository.findAll().stream()
                        .filter(ts -> ts.getDriver() != null
                                && ts.getDriver().getId().equals(d.getId())
                                && YearMonth.from(ts.getCreatedAt()).equals(yearMonth))
                        .map(TripSalary::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Tổng lương
                BigDecimal total = baseSalary.add(tripSalary).subtract(advance);

                report.add(SalaryReportResponse.builder()
                        .driverId(d.getId())
                        .driverName(d.getFullName())
                        .baseSalary(baseSalary.doubleValue())
                        .tripSalary(tripSalary.doubleValue())
                        .advancePayment(advance.doubleValue())
                        .totalSalary(total.doubleValue())
                        .build());
            }

            return report;
        }
    }