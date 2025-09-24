    package com.example.warehouse.Controller;
    
    import com.example.warehouse.Dto.Request.TripRequest;
    import com.example.warehouse.Dto.Request.TruckRequest;
    import com.example.warehouse.Dto.Response.PendingTripResponse;
    import com.example.warehouse.Dto.Response.SalaryReportResponse;
    import com.example.warehouse.Dto.Response.TripResponse;
    import com.example.warehouse.Dto.Response.TruckResponse;
    import com.example.warehouse.Entity.*;
    import com.example.warehouse.Enum.DriverStatus;
    import com.example.warehouse.Repository.TripRepository;
    import com.example.warehouse.Service.WarehouseService;
    import io.jsonwebtoken.io.IOException;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;
            import org.springframework.web.multipart.MultipartFile;

    import java.io.File;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.nio.file.StandardCopyOption;
    import java.util.List;
    
    @RestController
    @RequestMapping("/api/warehouse")
    @RequiredArgsConstructor
    public class WarehouseController {

        private final WarehouseService warehouseService;
        private TripRepository tripRepository;

        // ================= PRODUCT CRUD =================

        // CREATE product
        @PostMapping("/products")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Product> createProduct(@RequestBody Product product) {
            return ResponseEntity.ok(warehouseService.createProduct(product));
        }

        // READ all products
        @GetMapping("/products")
        public ResponseEntity<List<Product>> getAllProducts() {
            return ResponseEntity.ok(warehouseService.getAllProducts());
        }


        // READ product by ID
        @GetMapping("/products/{id}")
        public ResponseEntity<Product> getProductById(@PathVariable Long id) {
            return ResponseEntity.ok(warehouseService.getProductById(id));
        }

        // UPDATE product
        @PutMapping("/products/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
            return ResponseEntity.ok(warehouseService.updateProduct(id, product));
        }

        // DELETE product
        @DeleteMapping("/products/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
            warehouseService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        }

        // ================== InventoryTransaction ==============
        // Nhập kho
        @PostMapping("/import/{productId}")
        public ResponseEntity<?> importStock(@PathVariable Long productId,
                                             @RequestParam int quantity,
                                             @RequestParam String createdBy,
                                             @RequestParam(required = false) String referenceCode) {
            return ResponseEntity.ok(warehouseService.importStock(productId, quantity, createdBy, referenceCode));
        }

        // Xuất kho
        @PostMapping("/export/{productId}")
        public ResponseEntity<?> exportStock(@PathVariable Long productId,
                                             @RequestParam int quantity,
                                             @RequestParam String createdBy,
                                             @RequestParam(required = false) String referenceCode) {
            return ResponseEntity.ok(warehouseService.exportStock(productId, quantity, createdBy, referenceCode));
        }

        // Điều chỉnh tồn kho (kiểm kê) - reason là optional
        @PostMapping("/adjust/{productId}")
        public ResponseEntity<?> adjustStock(@PathVariable Long productId,
                                             @RequestParam int newStock,
                                             @RequestParam String createdBy,
                                             @RequestParam(required = false) String reason) {
            String note = reason != null ? reason : "";
            return ResponseEntity.ok(warehouseService.adjustStock(productId, newStock, createdBy, note));
        }

        // Xem tồn kho tất cả sản phẩm
        @GetMapping("/stocks")
        public ResponseEntity<?> getAllStocks() {
            return ResponseEntity.ok(warehouseService.getAllStocks());
        }

        // Xem lịch sử giao dịch của 1 sản phẩm
        @GetMapping("/transactions/{productId}")
        public ResponseEntity<?> getTransactions(@PathVariable Long productId) {
            return ResponseEntity.ok(warehouseService.getTransactions(productId));
        }

        // ================== QUẢN LÝ CHI PHÍ ==================
        @PostMapping("/expenses")
        @PreAuthorize("hasRole('NHANVIEN')")
        public ResponseEntity<Expense> addExpense(@RequestBody Expense expense) {
            return ResponseEntity.ok(warehouseService.addExpense(expense));
        }

        @GetMapping("/expenses")
        @PreAuthorize("hasRole('NHANVIEN')")
        public ResponseEntity<List<Expense>> getExpenses() {
            return ResponseEntity.ok(warehouseService.getExpenses());
        }

        @GetMapping("/expenses/pending")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<Expense>> getPendingExpenses() {
            return ResponseEntity.ok(warehouseService.getPendingExpenses());
        }

        @PutMapping("/expenses/{id}/approve")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Expense> approveExpense(@PathVariable Long id) {
            return ResponseEntity.ok(warehouseService.approveExpense(id));
        }

        @PutMapping("/expenses/{id}/reject")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Expense> rejectExpense(@PathVariable Long id) {
            return ResponseEntity.ok(warehouseService.rejectExpense(id));
        }

        @PutMapping("/expenses/{id}")
        @PreAuthorize("hasRole('NHANVIEN')")
        public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @RequestBody Expense expense) {
            return ResponseEntity.ok(warehouseService.updateExpense(id, expense));
        }

        @DeleteMapping("/expenses/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
            warehouseService.deleteExpense(id);
            return ResponseEntity.noContent().build();
        }

        // ================== QUẢN LÝ CHUYẾN ĐI (TRIP) ==================
        @PostMapping("/trips")
        public ResponseEntity<Trip> createTrip(@RequestBody TripRequest request) {
            Trip trip = warehouseService.addTrip(request);
            return ResponseEntity.ok(trip);
        }

        // Lấy danh sách chuyến đi chờ duyệt
        @GetMapping("/trips/pending")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<PendingTripResponse>> getPendingTrips() {
            List<PendingTripResponse> pendingTrips = warehouseService.getPendingTrips();
            System.out.println("Danh Sách Chuyến Đi Chờ Duyệt: " + pendingTrips.size());
            return ResponseEntity.ok(pendingTrips);
        }

        // Lấy toàn bộ chuyến đi
        @GetMapping("/trips")
        public ResponseEntity<List<TripResponse>> getTrips() {
            List<TripResponse> trips = warehouseService.getAllTrips();
            return ResponseEntity.ok(trips);
        }


        // Lấy chi tiết chuyến đi
        @GetMapping("/trips/{id}")
        public ResponseEntity<Trip> getTripById(@PathVariable Long id) {
            return ResponseEntity.ok(warehouseService.getTripById(id));
        }

        // Phê duyệt chuyến đi
        @PutMapping("/trips/{id}/approve")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Trip> approveTrip(@PathVariable Long id) {
            return ResponseEntity.ok(warehouseService.approveTrip(id));
        }

        // Từ chối chuyến đi
        @PutMapping("/trips/{id}/reject")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Trip> rejectTrip(@PathVariable Long id) {
            return ResponseEntity.ok(warehouseService.rejectTrip(id));
        }

        // Nộp chuyến đi để phê duyệt
        @PostMapping("/trips/{id}/submit")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<String> submitTrip(@PathVariable("id") Long id) {
            warehouseService.submitTrip(id);
            return ResponseEntity.ok("Chuyến đi đã được nộp để phê duyệt");
        }

        @PutMapping("/trips/{id}")
        public ResponseEntity<Trip> updateTrip(@PathVariable Long id, @RequestBody TripRequest request) {
            return ResponseEntity.ok(warehouseService.updateTrip(id, request));
        }

        // Xóa chuyến đi
        @DeleteMapping("/trips/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
            warehouseService.deleteTrip(id);
            return ResponseEntity.noContent().build();
        }

        // ================== TÀI LIỆU ==================
        @PostMapping("/documents/upload")
        public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
            try {
                warehouseService.uploadDocument(file);
                return ResponseEntity.ok("Tài liệu đã được tải lên thành công");
            } catch (Exception e) {
                e.printStackTrace(); // Xem lỗi thật trong console
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Upload thất bại: " + e.getMessage());
            }
        }

        @GetMapping("/documents")
        public ResponseEntity<List<String>> listDocuments() {
            return ResponseEntity.ok(warehouseService.listDocuments());
        }

        @GetMapping("/documents/{filename}")
        public ResponseEntity<byte[]> downloadDocument(@PathVariable String filename) {
            byte[] fileData = warehouseService.downloadDocument(filename);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + filename)
                    .body(fileData);
        }

        @DeleteMapping("/documents/{filename}")
        public ResponseEntity<Void> deleteDocument(@PathVariable String filename) {
            warehouseService.deleteDocument(filename);
            return ResponseEntity.noContent().build();
        }

        // ================== QUẢN LÝ XE TẢI ==================
        @PostMapping("/trucks")
        public ResponseEntity<TruckResponse> addTruck(@RequestBody TruckRequest request) {
            return ResponseEntity.ok(warehouseService.addTruck(request));
        }

        @GetMapping("/trucks")
        public ResponseEntity<List<Truck>> getAllTrucks() {
            return ResponseEntity.ok(warehouseService.getAllTrucks());
        }

        @GetMapping("/trucks/{id}")
        public ResponseEntity<Truck> getTruckById(@PathVariable Long id) {
            return ResponseEntity.ok(warehouseService.getTruckById(id));
        }

        @PutMapping("/trucks/{id}")
        public TruckResponse updateTruck(@PathVariable Long id, @RequestBody TruckRequest request) {
            return warehouseService.updateTruck(id, request);
        }

        @DeleteMapping("/trucks/{id}")
        public ResponseEntity<Void> deleteTruck(@PathVariable Long id) {
            warehouseService.deleteTruck(id);
            return ResponseEntity.noContent().build();
        }

        // API riêng chỉ đổi trạng thái xe tải
        @PutMapping("/trucks/{id}/status")
        public ResponseEntity<Truck> updateTruckStatus(@PathVariable Long id, @RequestParam String status) {
            Truck updatedTruck = warehouseService.updateTruckStatus(id, status);
            return ResponseEntity.ok(updatedTruck);
        }

        // ================== DRIVER ==================
        @PostMapping("/drivers")
        public ResponseEntity<Driver> addDriver(@RequestBody Driver driver) {
            return ResponseEntity.ok(warehouseService.addDriver(driver));
        }

        @GetMapping("/drivers")
        public ResponseEntity<List<Driver>> getAllDrivers() {
            return ResponseEntity.ok(warehouseService.getAllDrivers());
        }

        @GetMapping("/drivers/{id}")
        public ResponseEntity<Driver> getDriverById(@PathVariable Long id) {
            return ResponseEntity.ok(warehouseService.getDriverById(id));
        }

        @PutMapping("/drivers/{id}")
        public ResponseEntity<Driver> updateDriver(@PathVariable Long id, @RequestBody Driver driver) {
            return ResponseEntity.ok(warehouseService.updateDriver(id, driver));
        }

        @DeleteMapping("/drivers/{id}")
        public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
            warehouseService.deleteDriver(id);
            return ResponseEntity.noContent().build();
        }

        // đổi trạng thái Driver
        @PutMapping("/drivers/{id}/status")
        public ResponseEntity<Driver> updateDriverStatus(@PathVariable Long id, @RequestParam DriverStatus status) {
            return ResponseEntity.ok(warehouseService.updateDriverStatus(id, status));
        }

        // lấy danh sách theo trạng thái
        @GetMapping("/drivers/status/{status}")
        public ResponseEntity<List<Driver>> getDriversByStatus(@PathVariable DriverStatus status) {
            return ResponseEntity.ok(warehouseService.getDriversByStatus(status));
        }

        // ----------------- EXPENSE TYPE -----------------
        @GetMapping("/expense-types")
        public List<ExpenseType> getAllExpenseTypes() {
            return warehouseService.getAllExpenseTypes();
        }

        @PostMapping("/expense-types")
        public ExpenseType createExpenseType(@RequestBody ExpenseType expenseType) {
            return warehouseService.createExpenseType(expenseType);
        }

        @PutMapping("/expense-types/{id}")
        public ExpenseType updateExpenseType(@PathVariable Long id, @RequestBody ExpenseType expenseType) {
            return warehouseService.updateExpenseType(id, expenseType);
        }

        @DeleteMapping("/expense-types/{id}")
        public void deleteExpenseType(@PathVariable Long id) {
            warehouseService.deleteExpenseType(id);
        }

        // ================== ROUTE ==================
        @GetMapping("/routes")
        public ResponseEntity<List<Route>> getAllRoutes() {
            return ResponseEntity.ok(warehouseService.getAllRoutes());
        }

        @GetMapping("/routes/{routeCode}")
        public ResponseEntity<Route> getRouteByCode(@PathVariable String routeCode) {
            return ResponseEntity.ok(warehouseService.getRouteByCode(routeCode));
        }

        @PostMapping("/routes")
        public ResponseEntity<Route> createRoute(@RequestBody Route route) {
            return ResponseEntity.ok(warehouseService.createRoute(route));
        }

        @PutMapping("/routes/{routeCode}")
        public ResponseEntity<Route> updateRoute(@PathVariable String routeCode, @RequestBody Route route) {
            return ResponseEntity.ok(warehouseService.updateRoute(routeCode, route));
        }

        @DeleteMapping("/routes/{routeCode}")
        public ResponseEntity<Void> deleteRoute(@PathVariable String routeCode) {
            warehouseService.deleteRoute(routeCode);
            return ResponseEntity.noContent().build();
        }

        // ================== QUẢN LÝ LỊCH TRÌNH (SCHEDULE) ==================
        @GetMapping("/schedules")
        public ResponseEntity<List<Schedule>> getAllSchedules() {
            return ResponseEntity.ok(warehouseService.getAllSchedules());
        }

        @PostMapping("/schedules")
        public ResponseEntity<Schedule> createSchedule(@RequestBody Schedule schedule) {
            return ResponseEntity.ok(warehouseService.createSchedule(schedule));
        }

        @GetMapping("/schedules/{id}")
        public ResponseEntity<Schedule> getScheduleById(@PathVariable Long id) {
            return ResponseEntity.ok(warehouseService.getScheduleById(id));
        }

        @PutMapping("/schedules/{id}")
        public ResponseEntity<Schedule> updateSchedule(@PathVariable Long id, @RequestBody Schedule schedule) {
            return ResponseEntity.ok(warehouseService.updateSchedule(id, schedule));
        }

        @DeleteMapping("/schedules/{id}")
        public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
            warehouseService.deleteSchedule(id);
            return ResponseEntity.noContent().build();
        }

        // duyệt
        @PutMapping("/schedules/{id}/approve")
        public ResponseEntity<Schedule> approveSchedule(@PathVariable Long id) {
            return ResponseEntity.ok(warehouseService.approveSchedule(id));
        }

        // từ chối
        @PutMapping("/schedules/{id}/reject")
        public ResponseEntity<Schedule> rejectSchedule(@PathVariable Long id) {
            return ResponseEntity.ok(warehouseService.rejectSchedule(id));
        }

        // ================== PHÊ DUYỆT LỊCH TRÌNH ==================
        @PostMapping("/schedules/{id}/submit")
        public ResponseEntity<Schedule> submitSchedule(@PathVariable Long id) {
            Schedule submittedSchedule = warehouseService.submitSchedule(id);
            return ResponseEntity.ok(submittedSchedule);
        }


        // ================== SALARY / TRIP SALARY ==================
        @PostMapping("/trip-salaries")
        public ResponseEntity<TripSalary> addTripSalary(@RequestParam Long tripId,
                                                        @RequestParam Long driverId,
                                                        @RequestParam Double amount) {
            TripSalary ts = warehouseService.createTripSalary(tripId, driverId, amount);
            return ResponseEntity.ok(ts);
        }

        @GetMapping("/trip-salaries")
        public ResponseEntity<List<TripSalary>> getTripSalaries() {
            return ResponseEntity.ok(warehouseService.getTripSalaries());
        }

        @GetMapping("/trip-salaries/{id}")
        public ResponseEntity<TripSalary> getTripSalaryById(@PathVariable Long id) {
            return ResponseEntity.ok(warehouseService.getTripSalaryById(id));
        }

        // ================== SALARY CONFIG ==================
        @PostMapping("/config")
        public SalaryConfig addSalaryConfig(@RequestParam Long driverId,
                                            @RequestParam String month,
                                            @RequestParam Double baseSalary,
                                            @RequestParam Double advance) {
            return warehouseService.createSalaryConfig(driverId, month, baseSalary, advance);
        }

       
    }
