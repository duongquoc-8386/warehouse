package com.example.warehouse.Controller;

import com.example.warehouse.Dto.Request.ScheduleRequest;
import com.example.warehouse.Dto.Request.TruckRequest;
import com.example.warehouse.Dto.Response.PendingScheduleResponse;
import com.example.warehouse.Dto.Response.TruckResponse;
import com.example.warehouse.Entity.*;
import com.example.warehouse.Enum.DriverStatus;
import com.example.warehouse.Repository.XuatKhoRepository;
import com.example.warehouse.Service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.warehouse.Entity.Schedule;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseController {

    @Autowired
    private XuatKhoRepository xuatKhoRepository;
    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService service) {
        this.warehouseService = service;
    }

    // ================== NHẬP KHO ==================
    @PostMapping("/nhap")
    public NhapKho nhapKho(@RequestBody NhapKho nhapKho) {
        return warehouseService.nhapKho(nhapKho);
    }

    @PutMapping("/nhap/{id}")
    public NhapKho updateNhapKho(@PathVariable Long id, @RequestBody NhapKho nhapKho) {
        return warehouseService.updateNhapKho(id, nhapKho);
    }

    @DeleteMapping("/nhap/{id}")
    public ResponseEntity<?> deleteNhapKho(@PathVariable Long id) {
        warehouseService.deleteNhapKho(id);
        return ResponseEntity.ok("Xóa nhập kho thành công");
    }

    @GetMapping("/nhap")
    public List<NhapKho> getAllNhapKho() {
        return warehouseService.getAllNhapKho();
    }

    // ================== XUẤT KHO ==================
    @PostMapping("/xuat")
    public ResponseEntity<XuatKho> xuatKho(@RequestBody XuatKho xuatKho) {
        XuatKho saved = xuatKhoRepository.save(xuatKho);
        return ResponseEntity.ok(saved);
    }


    @PutMapping("/xuat/{id}")
    public XuatKho updateXuatKho(@PathVariable Long id, @RequestBody XuatKho xuatKho) {
        return warehouseService.updateXuatKho(id, xuatKho);
    }

    @DeleteMapping("/xuat/{id}")
    public ResponseEntity<String> deleteXuat(@PathVariable Long id) {
        if (xuatKhoRepository.existsById(id)) { //
            xuatKhoRepository.deleteById(id);
            return ResponseEntity.ok("Xóa thành công");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ID không tồn tại");
        }
    }


    @GetMapping("/xuat")
    public List<XuatKho> getAllXuatKho() {
        return warehouseService.getAllXuatKho();
    }


    // ================== TỒN KHO ==================
    @GetMapping("/ton")
    public List<TonKho> getTonKho() {
        return warehouseService.getTonKho();
    }

    @GetMapping("/ton/search")
    public List<TonKho> searchTonKho(@RequestParam String keyword) {
        return warehouseService.searchTonKho(keyword);
    }

    @GetMapping("/ton/filter")
    public List<TonKho> filterTonKho(
            @RequestParam(required = false) String loaiHang,
            @RequestParam(required = false) String tenHang,
            @RequestParam(required = false) Integer minSoLuong,
            @RequestParam(required = false) Integer maxSoLuong
    ) {
        return warehouseService.filterTonKho(loaiHang, tenHang, minSoLuong, maxSoLuong);
    }

    @GetMapping("/ton/summary")
    public ResponseEntity<?> getTonKhoSummary() {
        return ResponseEntity.ok(warehouseService.getTonKhoSummary());
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

    // ================== QUẢN LÝ LỊCH TRÌNH ==================

    // Tạo Lịch Trình
    @PostMapping("/schedules")
    public Schedule createSchedule(@RequestBody ScheduleRequest request) {
        Schedule schedule = new Schedule();
        schedule.setTitle(request.getTitle());
        schedule.setStartLocation(request.getStartLocation());
        schedule.setEndLocation(request.getEndLocation());
        schedule.setDate(request.getDate());

        return warehouseService.addSchedule(request);
    }

    // Lấy danh sách lịch trình chờ duyệt
    @GetMapping("/pending/schedules")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PendingScheduleResponse>> getPendingSchedules() {
        List<PendingScheduleResponse> pendingSchedules  = warehouseService.getPendingSchedules();
        System.out.println("Danh Sách Lịch Trình Chờ Duyệt: " + pendingSchedules.size());
        return ResponseEntity.ok(warehouseService.getPendingSchedules());
    }


    //Lấy toàn bộ lịch trình
    @GetMapping("/schedules")
    public ResponseEntity<List<Schedule>> getSchedules() {
        return ResponseEntity.ok(warehouseService.getSchedules());
    }

    //Phê duyệt
    @PutMapping("/schedules/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Schedule> approveSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.approveSchedule(id));
    }
 //Từ chối
    @PutMapping("/schedules/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Schedule> rejectSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.rejectSchedule(id));
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
        // ================== PHÊ DUYỆT LỊCH TRÌNH ==================
        @PostMapping("/schedules/{id}/submit")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<String> submitSchedule (@PathVariable Long id){
            warehouseService.submitSchedule(id);
            return ResponseEntity.ok("Lịch trình đã được nộp để phê duyệt");
        }


// ================== QUẢN LÝ XE TẢI ==================
@PostMapping("/trucks")
public ResponseEntity<TruckResponse> addTruck(@RequestBody TruckRequest request) {
    return ResponseEntity.ok(warehouseService   .addTruck(request));
}



    @GetMapping("/trucks")
        public ResponseEntity<List<Truck>> getAllTrucks () {
            return ResponseEntity.ok(warehouseService.getAllTrucks());
        }

        @GetMapping("/trucks/{id}")
        public ResponseEntity<Truck> getTruckById (@PathVariable Long id){
            return ResponseEntity.ok(warehouseService.getTruckById(id));
        }

    @PutMapping("/trucks/{id}")
    public TruckResponse updateTruck(@PathVariable Long id, @RequestBody TruckRequest request) {
        return warehouseService.updateTruck(id, request);
    }


    @DeleteMapping("/trucks/{id}")
        public ResponseEntity<Void> deleteTruck (@PathVariable Long id){
            warehouseService.deleteTruck(id);
            return ResponseEntity.noContent().build();
        }

// API riêng chỉ đổi trạng thái xe tải
        @PutMapping("/trucks/{id}/status")
        public ResponseEntity<Truck> updateTruckStatus (@PathVariable Long id, @RequestParam String status){
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


    // Get all routes
    @GetMapping("/routes")
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(warehouseService.getAllRoutes());
    }

    // Get route by code
    @GetMapping("/routes/{routeCode}")
    public ResponseEntity<Route> getRouteByCode(@PathVariable String routeCode) {
        return ResponseEntity.ok(warehouseService.getRouteByCode(routeCode));
    }

    // Create new route
    @PostMapping("/routes")
    public ResponseEntity<Route> createRoute(@RequestBody Route route) {
        return ResponseEntity.ok(warehouseService.createRoute(route));
    }

    // Update route
    @PutMapping("/routes/{routeCode}")
    public ResponseEntity<Route> updateRoute(@PathVariable String routeCode, @RequestBody Route route) {
        return ResponseEntity.ok(warehouseService.updateRoute(routeCode, route));
    }

    // Delete route
    @DeleteMapping("/routes/{routeCode}")
    public ResponseEntity<Void> deleteRoute(@PathVariable String routeCode) {
        warehouseService.deleteRoute(routeCode);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/config")
    public SalaryConfig addSalaryConfig(@RequestParam Long driverId,
                                        @RequestParam String month,
                                        @RequestParam Double baseSalary,
                                        @RequestParam Double advance) {
        return warehouseService.createSalaryConfig(driverId, month, baseSalary, advance);
    }

    @PostMapping("/schedule-salary")
    public ScheduleSalary addScheduleSalary(@RequestParam Long scheduleId,
                                            @RequestParam Long driverId,
                                            @RequestParam Double amount) {
        return warehouseService.createScheduleSalary(scheduleId, driverId, amount);
    }
}



