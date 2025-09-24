package com.example.warehouse.Service;

import com.example.warehouse.Dto.Request.TripRequest;
import com.example.warehouse.Dto.Request.TruckRequest;
import com.example.warehouse.Dto.Response.PendingTripResponse;
import com.example.warehouse.Dto.Response.TripResponse;
import com.example.warehouse.Dto.Response.TruckResponse;
import com.example.warehouse.Entity.*;
import com.example.warehouse.Entity.ExpenseType;
import com.example.warehouse.Enum.*;
import com.example.warehouse.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    // ---------------- repositories ----------------
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ExpenseRepository expenseRepository;
    private final TripRepository tripRepository;
    private final DriverRepository driverRepository;
    private final SalaryConfigRepository salaryConfigRepository;
    private final TripSalaryRepository tripSalaryRepository;
    private final DocumentRepository documentRepository;
    private final TruckRepository truckRepository;
    private final ExpenseTypeRepository expenseTypeRepository;
    private final RouteRepository routeRepository;
    private final ScheduleRepository scheduleRepository;

    // ---------------- utils ----------------
    private String formatMoney(Double amount) {
        if (amount == null) return "0 VND";
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(amount) + " VND";
    }

    // ======================================================
    // INVENTORY
    // ======================================================
    @Transactional

    private String generateReferenceCode(TransactionType type) {
        String prefix = switch (type) {
            case IMPORT -> "IMP";
            case EXPORT -> "EXP";
            case RETURN -> "RET";
            case ADJUSTMENT -> "ADJ";
        };

        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = inventoryTransactionRepository.count() + 1; // hoặc count theo ngày
        return prefix + "-" + datePart + "-" + String.format("%04d", count);
    }

    public InventoryTransaction createTransaction(Long productId, TransactionType type, int quantity, String createdBy, String referenceCode, String note) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + productId));

        if (type == TransactionType.EXPORT) {
            int available = computeStock(productId);
            if (available < quantity) {
                throw new RuntimeException("Không đủ hàng trong kho. Hiện có: " + available);
            }
        }

        InventoryTransaction tx = InventoryTransaction.builder()
                .product(product)
                .type(type)
                .quantity(quantity)
                .createdBy(createdBy)
                .referenceCode(referenceCode != null ? referenceCode : generateReferenceCode(type))
                .note(note != null ? note : "")
                .createdAt(LocalDateTime.now())
                .build();


        try {
            Integer cur = Optional.ofNullable(product.getCurrentStock()).orElse(0);
            if (type == TransactionType.IMPORT) product.setCurrentStock(cur + quantity);
            else if (type == TransactionType.EXPORT) product.setCurrentStock(cur - quantity);
            else if (type == TransactionType.ADJUSTMENT) product.setCurrentStock(cur + quantity);
            productRepository.save(product);
        } catch (Exception ignored) {}

        return inventoryTransactionRepository.save(tx);
    }

    public InventoryTransaction importStock(Long productId, int quantity, String createdBy, String note) {
        return createTransaction(productId, TransactionType.IMPORT, quantity, createdBy, null, note);
    }

    public InventoryTransaction exportStock(Long productId, int quantity, String createdBy, String note) {
        return createTransaction(productId, TransactionType.EXPORT, quantity, createdBy, null, note);
    }

    public InventoryTransaction adjustStock(Long productId, int newStock, String createdBy, String note) {
        int current = computeStock(productId);
        int diff = newStock - current;
        return createTransaction(productId, TransactionType.ADJUSTMENT, diff, createdBy, "ADJ-" + UUID.randomUUID().toString().substring(0, 8), note);
    }

    public int computeStock(Long productId) {
        Optional<Product> pOpt = productRepository.findById(productId);
        if (pOpt.isPresent()) {
            Product p = pOpt.get();
            try {
                Integer s = p.getCurrentStock();
                if (s != null) return s;
            } catch (Exception ignored) {}
        }

        List<InventoryTransaction> txs = inventoryTransactionRepository.findByProductId(productId);
        return txs.stream()
                .mapToInt(t -> t.getType() == TransactionType.IMPORT ? t.getQuantity() : -t.getQuantity())
                .sum();
    }

    public List<InventoryTransaction> getTransactions(Long productId) {
        return inventoryTransactionRepository.findByProductId(productId);
    }

    public List<InventoryTransaction> getAllTransactions() {
        return inventoryTransactionRepository.findAll();
    }

    public List<Product> getAllStocks() {
        return productRepository.findAll();
    }

    // ======================================================
    // EXPENSE
    // ======================================================
    public Expense addExpense(Expense expense) {
        if (expense.getExpenseType() != null && expense.getExpenseType().getId() != null) {
            ExpenseType et = expenseTypeRepository.findById(expense.getExpenseType().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy loại chi phí"));
            expense.setExpenseType(et);
        }

        if (expense.getCreatedAt() == null) expense.setCreatedAt(LocalDateTime.now());
        if (expense.getStatus() == null) expense.setStatus(ExpenseStatus.PENDING);
        return expenseRepository.save(expense);
    }

    public List<Expense> getExpenses() {
        return expenseRepository.findAll();
    }

    public List<Expense> getPendingExpenses() {
        return expenseRepository.findAll().stream()
                .filter(e -> e.getStatus() == ExpenseStatus.PENDING)
                .collect(Collectors.toList());
    }

    public Expense approveExpense(Long id) {
        return updateExpenseStatus(id, ExpenseStatus.APPROVED);
    }

    public Expense rejectExpense(Long id) {
        return updateExpenseStatus(id, ExpenseStatus.REJECTED);
    }

    private Expense updateExpenseStatus(Long id, ExpenseStatus status) {
        Expense exp = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi phí"));
        exp.setStatus(status);
        return expenseRepository.save(exp);
    }

    public List<ExpenseType> getAllExpenseTypes() {
        return expenseTypeRepository.findAll();
    }

    public ExpenseType createExpenseType(ExpenseType expenseType) {
        return expenseTypeRepository.save(expenseType);
    }

    public ExpenseType updateExpenseType(Long id, ExpenseType expenseType) {
        return expenseTypeRepository.findById(id).map(et -> {
            et.setName(expenseType.getName());
            return expenseTypeRepository.save(et);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy loại chi phí với ID: " + id));
    }

    public void deleteExpenseType(Long id) {
        expenseTypeRepository.deleteById(id);
    }
    public Expense updateExpense(Long id, Expense expense) {
        Expense existing = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi phí"));

        if (expense.getExpenseType() != null && expense.getExpenseType().getId() != null) {
            ExpenseType et = expenseTypeRepository.findById(expense.getExpenseType().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy loại chi phí"));
            existing.setExpenseType(et);
        }

        existing.setDescription(expense.getDescription());
        existing.setAmount(expense.getAmount());
        existing.setStatus(expense.getStatus());

        return expenseRepository.save(existing);
    }

    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy chi phí");
        }
        expenseRepository.deleteById(id);
    }
    // ======================================================
    // DOCUMENT
    // ======================================================
    public String uploadDocument(MultipartFile file) throws IOException {
        Document doc = new Document();
        doc.setFileName(file.getOriginalFilename());
        doc.setContentType(file.getContentType());
        doc.setData(file.getBytes());
        documentRepository.save(doc);

        Path path = Paths.get("uploads/" + file.getOriginalFilename());
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        return "Upload file thành công: " + path.toString();
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
    private final Path rootLocation = Paths.get("uploads");

    // Liệt kê file
    public List<String> listDocuments() {
        try (Stream<Path> stream = Files.walk(this.rootLocation, 1)) {
            return stream.filter(path -> !path.equals(this.rootLocation))
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc thư mục tài liệu", e);
        }
    }

    // Download file
    public byte[] downloadDocument(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tải file: " + filename, e);
        }
    }

    // Xóa file
    public void deleteDocument(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Không thể xóa file: " + filename, e);
        }
    }
    // ======================================================
    // TRUCK
    // ======================================================
    public TruckResponse addTruck(TruckRequest request) {
        Driver driver = null;
        if (request.getDriverId() != null) {
            driver = driverRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài xế!"));
        }

        Truck truck = new Truck();
        truck.setLicensePlate(request.getLicensePlate());
        truck.setCapacity(request.getCapacity());
        truck.setStatus(TruckStatus.valueOf(request.getStatus().toUpperCase()));
        truck.setDriver(driver);

        Truck saved = truckRepository.save(truck);

        return TruckResponse.builder()
                .id(saved.getId())
                .licensePlate(saved.getLicensePlate())
                .status(saved.getStatus().name())
                .build();
    }

    public List<Truck> getAllTrucks() {
        return truckRepository.findAll();
    }

    public Truck getTruckById(Long id) {
        return truckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe tải!"));
    }

    public TruckResponse updateTruck(Long id, TruckRequest request) {
        Truck existing = truckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe tải!"));

        existing.setLicensePlate(request.getLicensePlate());
        existing.setCapacity(request.getCapacity());
        existing.setStatus(TruckStatus.valueOf(request.getStatus().toUpperCase()));
        if (request.getDriverId() != null) {
            Driver d = driverRepository.findById(request.getDriverId()).orElseThrow(() -> new RuntimeException("Không tìm thấy tài xế"));
            existing.setDriver(d);
        }

        Truck saved = truckRepository.save(existing);
        return TruckResponse.builder()
                .id(saved.getId())
                .licensePlate(saved.getLicensePlate())
                .status(saved.getStatus().name())
                .build();
    }

    public String deleteTruck(Long id) {
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe tải với ID: " + id));
        truckRepository.delete(truck);
        return "Đã xóa xe tải thành công! Biển số: " + truck.getLicensePlate();
    }

    public Truck updateTruckStatus(Long truckId, String status) {
        Truck truck = truckRepository.findById(truckId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe tải"));
        truck.setStatus(TruckStatus.valueOf(status.toUpperCase()));
        return truckRepository.save(truck);
    }

    // ======================================================
    // DRIVER
    // ======================================================
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài xế!"));
    }

    public Driver addDriver(Driver driver) {
        driver.setCreatedAt(LocalDate.now());
        if (driver.getStatus() == null) {
            driver.setStatus(DriverStatus.AVAILABLE);
        }
        return driverRepository.save(driver);
    }

    public Driver updateDriver(Long id, Driver driverDetails) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài xế"));
        driver.setEmployeeCode(driverDetails.getEmployeeCode());
        driver.setFullName(driverDetails.getFullName());
        driver.setPhoneNumber(driverDetails.getPhoneNumber());
        driver.setDateOfBirth(driverDetails.getDateOfBirth());
        driver.setNote(driverDetails.getNote());
        driver.setCreatedAt(driverDetails.getCreatedAt());
        driver.setStatus(driverDetails.getStatus());
        driver.setBasicSalary(driverDetails.getBasicSalary());
        driver.setAdvancePayment(driverDetails.getAdvancePayment());
        return driverRepository.save(driver);
    }

    public void deleteDriver(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new RuntimeException("Không thể xóa! Tài xế với ID " + id + " không tồn tại.");
        }
        driverRepository.deleteById(id);
    }

    public Driver updateDriverStatus(Long id, DriverStatus status) {
        Driver driver = getDriverById(id);
        driver.setStatus(status);
        return driverRepository.save(driver);
    }

    public List<Driver> getDriversByStatus(DriverStatus status) {
        return driverRepository.findByStatus(status);
    }


    //schuldes
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    // Phê duyệt Schedule
    public Schedule approveSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        schedule.setStatus(ScheduleStatus.APPROVED);
        return scheduleRepository.save(schedule);
    }

    // Từ chối Schedule
    public Schedule rejectSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        schedule.setStatus(ScheduleStatus.REJECTED);
        return scheduleRepository.save(schedule);
    }
    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id " + id));
    }

    public Schedule createSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public Schedule updateSchedule(Long id, Schedule schedule) {
        Schedule existing = getScheduleById(id);
        existing.setTitle(schedule.getTitle());
        existing.setDate(schedule.getDate());
        existing.setStartLocation(schedule.getStartLocation());
        existing.setEndLocation(schedule.getEndLocation());
        existing.setCost(schedule.getCost());
        existing.setStatus(schedule.getStatus());
        existing.setDriver(schedule.getDriver());
        existing.setTruck(schedule.getTruck());
        return scheduleRepository.save(schedule);
    }


    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    public Schedule submitSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Schedule với id = " + id));

        // Ví dụ: cập nhật trạng thái thành "SUBMITTED"
        schedule.setStatus(ScheduleStatus.SUBMITTED);
        return scheduleRepository.save(schedule);
    }


    // ======================================================
    // ROUTE
    // ======================================================
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Route getRouteByCode(String routeCode) {
        return routeRepository.findByRouteCode(routeCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tuyến đường với mã: " + routeCode));
    }

    public Route createRoute(Route route) {
        return routeRepository.save(route);
    }

    public Route updateRoute(String routeCode, Route route) {
        Route entity = routeRepository.findByRouteCode(routeCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tuyến đường với mã: " + routeCode));
        entity.setRouteName(route.getRouteName());
        entity.setTripPrice(route.getTripPrice());
        entity.setNote(route.getNote());
        return routeRepository.save(entity);
    }

    public void deleteRoute(String routeCode) {
        Route entity = routeRepository.findByRouteCode(routeCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tuyến đường với mã: " + routeCode));
        routeRepository.delete(entity);
    }

    // ======================================================
    // TRIP
    // ======================================================



    public Trip addTrip(TripRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new RuntimeException("Tiêu đề chuyến đi không được để trống!");
        }
        if (request.getStartTime() == null || request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Thời gian bắt đầu phải từ hiện tại trở đi!");
        }

        Truck truck = truckRepository.findById(request.getTruckId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe tải"));

        Trip trip = new Trip();
        trip.setTitle(request.getTitle());
        trip.setStartTime(request.getStartTime());
        trip.setEndTime(request.getEndTime());
        trip.setDescription(Optional.ofNullable(request.getDescription()).orElse("Chưa cập nhật"));
        trip.setCost(Optional.ofNullable(request.getCost()).orElse(0.0));
        trip.setStatus(request.getStatus() != null ? TripStatus.valueOf(request.getStatus()) : TripStatus.PENDING);
        trip.setCreatedAt(LocalDateTime.now());
        trip.setProofDocumentPath("chưa có tài liệu");
        trip.setTruck(truck);
        trip.setDriver(request.getDriverId() != null ? driverRepository.findById(request.getDriverId()).orElse(null) : null);
        trip.setRoute(request.getRouteId() != null ? routeRepository.findById(request.getRouteId()).orElse(null) : null);

        return tripRepository.save(trip);
    }


    public List<TripResponse> getAllTrips() {
        List<Trip> trips = tripRepository.findAll(); // entity
        return trips.stream()
                .map(TripResponse::fromEntity) // chuyển sang DTO
                .collect(Collectors.toList());
    }


    public Trip getTripById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi với ID: " + id));
    }

    public void deleteTrip(Long id) {
        if (!tripRepository.existsById(id)) {
            throw new RuntimeException("Không tồn tại chuyến đi với ID: " + id);
        }
        tripRepository.deleteById(id);
    }

    public Trip approveTrip(Long id) {
        return updateTripStatus(id, TripStatus.APPROVED);
    }

    public Trip rejectTrip(Long id) {
        return updateTripStatus(id, TripStatus.REJECTED);
    }

    public Trip submitTrip(Long id) {
        return updateTripStatus(id, TripStatus.SUBMITTED);
    }

    private Trip updateTripStatus(Long id, TripStatus status) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi!"));
        trip.setStatus(status);
        return tripRepository.save(trip);
    }

    public Trip updateTrip(Long id, TripRequest request) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi"));

        trip.setRoute(routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tuyến đường")));
        trip.setTruck(truckRepository.findById(request.getTruckId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe tải")));
        trip.setDriver(driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài xế")));
        trip.setStartTime(request.getStartTime());
        trip.setEndTime(request.getEndTime());

        return tripRepository.save(trip);
    }

    public List<PendingTripResponse> getPendingTrips() {
        List<Trip> trips = tripRepository.findByStatus(TripStatus.PENDING);
        return trips.stream()
                .map(t -> new PendingTripResponse(
                        t.getId(),
                        t.getTitle(),
                        t.getStartTime(),
                        t.getEndTime(),
                        t.getStatus().name()
                ))
                .collect(Collectors.toList());
    }

    // ======================================================
    // SALARY
    // ======================================================
    public SalaryConfig createSalaryConfig(Long driverId, String month, Double baseSalary, Double advance) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài xế"));
        SalaryConfig config = SalaryConfig.builder()
                .driver(driver)
                .month(month)
                .baseSalary(BigDecimal.valueOf(baseSalary))
                .advance(BigDecimal.valueOf(advance))
                .build();
        return salaryConfigRepository.save(config);
    }



    public TripSalary createTripSalary(Long tripId, Long driverId, Double amount) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài xế"));
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi"));
        TripSalary tripSalary = TripSalary.builder()
                .driver(driver)
                .trip(trip)
                .amount(BigDecimal.valueOf(amount))
                .createdAt(LocalDate.now())
                .build();
        return tripSalaryRepository.save(tripSalary);
    }

    // ================== TRIP SALARY ==================
    public List<TripSalary> getTripSalaries() {
        return tripSalaryRepository.findAll();
    }

    public TripSalary getTripSalaryById(Long id) {
        return tripSalaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy TripSalary với ID: " + id));
    }
    // ================= PRODUCT CRUD =================

    // CREATE
    public Product createProduct(Product product) {
        if (product.getCurrentStock() == null) product.setCurrentStock(0);
        return productRepository.save(product);
    }

    // READ all
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // READ by id
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + id));
    }

    // UPDATE
    public Product updateProduct(Long id, Product updated) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + id));

        product.setName(updated.getName());
        product.setCurrentStock(updated.getCurrentStock());

        return productRepository.save(product);
    }

    // DELETE
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy sản phẩm với id: " + id);


    }
        productRepository.deleteById(id);
    }

}
