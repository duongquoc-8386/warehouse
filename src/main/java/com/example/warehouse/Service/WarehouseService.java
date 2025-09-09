package com.example.warehouse.Service;

import com.example.warehouse.Dto.Request.ScheduleRequest;
import com.example.warehouse.Dto.Request.TruckRequest;
import com.example.warehouse.Dto.Response.PendingScheduleResponse;
import com.example.warehouse.Dto.Response.TruckResponse;
import com.example.warehouse.Entity.*;
import com.example.warehouse.Enum.*;
import com.example.warehouse.Repository.*;
import com.example.warehouse.Validator.ProductCategoryValidator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    private final NhapKhoRepository nhapKhoRepository;
    private final XuatKhoRepository xuatKhoRepository;
    private final TonKhoRepository tonKhoRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ExpenseRepository expenseRepository;
    private final ScheduleRepository scheduleRepository;
    private final DriverRepository driverRepository;
    private final SalaryConfigRepository salaryConfigRepository;
    private final ScheduleSalaryRepository scheduleSalaryRepository;


    @Autowired
    private DocumentRepository documentRepository;

    private TruckRepository truckRepository;

    private ExpenseTypeRepository expenseTypeRepository;

    private final RouteRepository routeRepository;


    public WarehouseService(NhapKhoRepository nhapKhoRepository,
                            XuatKhoRepository xuatKhoRepository,
                            TonKhoRepository tonKhoRepository,
                            ProductRepository productRepository,
                            ProductVariantRepository productVariantRepository,
                            ExpenseRepository expenseRepository,
                            ScheduleRepository scheduleRepository,
                            DocumentRepository documentRepository,
                            TruckRepository truckRepository,
                            DriverRepository driverRepository,
                            ExpenseTypeRepository expenseTypeRepository,
                            RouteRepository routeRepository,
                            SalaryConfigRepository salaryconfigRepository,
                            ScheduleSalaryRepository scheduleSalaryRepository) {

        this.nhapKhoRepository = nhapKhoRepository;
        this.xuatKhoRepository = xuatKhoRepository;
        this.tonKhoRepository = tonKhoRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.expenseRepository = expenseRepository;
        this.scheduleRepository = scheduleRepository;
        this.documentRepository = documentRepository;
        this.truckRepository = truckRepository;
        this.driverRepository = driverRepository;
        this.expenseTypeRepository = expenseTypeRepository;
        this.routeRepository = routeRepository;
        this.salaryConfigRepository = salaryconfigRepository;
        this.scheduleSalaryRepository = scheduleSalaryRepository;
    }

    // ================== Nhập kho ==================
    public NhapKho nhapKho(NhapKho nhapKho) {
        if (nhapKho.getTenHangHoa() == null || nhapKho.getTenHangHoa().isBlank()) {
            throw new RuntimeException("Tên hàng hóa không được để trống!");
        }
        if (nhapKho.getSoLuong() == null || nhapKho.getSoLuong() <= 0) {
            throw new RuntimeException("Số lượng nhập phải lớn hơn 0!");
        }

        if (!ProductCategoryValidator.isValid(
                nhapKho.getTenHangHoa(),
                LoaiHang.valueOf(nhapKho.getLoaiHang().toUpperCase())
        )) {
            throw new IllegalArgumentException("Loại hàng '" + nhapKho.getLoaiHang() + "' không đúng với tên hàng '" + nhapKho.getTenHangHoa() + "'!");
        }
        NhapKho saved = nhapKhoRepository.save(nhapKho);

        TonKho ton = tonKhoRepository.findByTenHangHoaContainingIgnoreCase(nhapKho.getTenHangHoa())
                .stream().findFirst()
                .orElse(TonKho.builder()
                        .tenHangHoa(nhapKho.getTenHangHoa())
                        .loaiHang(nhapKho.getLoaiHang())
                        .soLuong(0)
                        .build());

        ton.setSoLuong(ton.getSoLuong() + nhapKho.getSoLuong());
        tonKhoRepository.save(ton);

        return saved;
    }

    // Lấy toàn bộ nhập kho
    public List<NhapKho> getAllNhapKho() {
        return nhapKhoRepository.findAll();
    }

    public NhapKho updateNhapKho(Long id, NhapKho updated) {
        NhapKho existing = nhapKhoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu nhập!"));

        int diff = updated.getSoLuong() - existing.getSoLuong();

        TonKho ton = tonKhoRepository.findByTenHangHoaContainingIgnoreCase(existing.getTenHangHoa())
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tồn kho!"));

        ton.setSoLuong(ton.getSoLuong() + diff);
        tonKhoRepository.save(ton);

        existing.setSoLuong(updated.getSoLuong());
        existing.setLoaiHang(updated.getLoaiHang());
        existing.setTenHangHoa(updated.getTenHangHoa());

        return nhapKhoRepository.save(existing);
    }

    public void deleteNhapKho(Long id) {
        NhapKho existing = nhapKhoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu nhập!"));

        TonKho ton = tonKhoRepository.findByTenHangHoaContainingIgnoreCase(existing.getTenHangHoa())
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tồn kho!"));

        ton.setSoLuong(ton.getSoLuong() - existing.getSoLuong());
        tonKhoRepository.save(ton);

        nhapKhoRepository.deleteById(id);
    }

    // ================== Xuất kho ==================
    public XuatKho xuatKho(XuatKho xuatKho) {
        if (xuatKho.getTenHangHoa() == null || xuatKho.getTenHangHoa().isBlank()) {
            throw new RuntimeException("Tên hàng hóa không được để trống!");
        }
        if (xuatKho.getSoLuong() == null || xuatKho.getSoLuong() <= 0) {
            throw new RuntimeException("Số lượng xuất phải lớn hơn 0!");
        }

        TonKho ton = tonKhoRepository.findByTenHangHoaContainingIgnoreCase(xuatKho.getTenHangHoa())
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hàng '" + xuatKho.getTenHangHoa() + "' trong kho!"));

        if (!ProductCategoryValidator.isValid(
                xuatKho.getTenHangHoa(),
                LoaiHang.valueOf(xuatKho.getLoaiHang().toUpperCase())
        )) {
            throw new IllegalArgumentException("Loại hàng '" + xuatKho.getLoaiHang() + "' không khớp với tên hàng '" + xuatKho.getTenHangHoa() + "'!");
        }

        if (ton.getSoLuong() < xuatKho.getSoLuong()) {
            throw new RuntimeException("Số lượng tồn (" + ton.getSoLuong() + ") không đủ để xuất " + xuatKho.getSoLuong() + "!");
        }

        ton.setSoLuong(ton.getSoLuong() - xuatKho.getSoLuong());
        tonKhoRepository.save(ton);

        return xuatKhoRepository.save(xuatKho);
    }

    // Lấy toàn bộ xuất kho
    public List<XuatKho> getAllXuatKho() {
        return xuatKhoRepository.findAll();
    }

    public XuatKho updateXuatKho(Long id, XuatKho updated) {
        XuatKho existing = xuatKhoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu xuất!"));

        TonKho ton = tonKhoRepository.findByTenHangHoaContainingIgnoreCase(existing.getTenHangHoa())
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tồn kho!"));

        int diff = updated.getSoLuong() - existing.getSoLuong();

        if (diff > 0 && ton.getSoLuong() < diff) {
            throw new RuntimeException("Không đủ hàng để cập nhật phiếu xuất!");
        }

        ton.setSoLuong(ton.getSoLuong() - diff);
        tonKhoRepository.save(ton);

        existing.setSoLuong(updated.getSoLuong());
        existing.setLoaiHang(updated.getLoaiHang());
        existing.setTenHangHoa(updated.getTenHangHoa());

        return xuatKhoRepository.save(existing);
    }

    public void deleteXuatKho(Long id) {
        XuatKho existing = xuatKhoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu xuất!"));

        TonKho ton = tonKhoRepository.findByTenHangHoaContainingIgnoreCase(existing.getTenHangHoa())
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tồn kho!"));

        ton.setSoLuong(ton.getSoLuong() + existing.getSoLuong());
        tonKhoRepository.save(ton);

        xuatKhoRepository.deleteById(id);
    }

    // ================== Tồn kho ==================
    public List<TonKho> getTonKho() {
        return tonKhoRepository.findAll();
    }

    // tồn kho
    public HashMap<String, Object> getTonKhoSummary() {
        HashMap<String, Object> summary = new HashMap<>();
        List<TonKho> tonList = tonKhoRepository.findAll();
        int total = tonList.stream().mapToInt(TonKho::getSoLuong).sum();
        summary.put("totalSoLuong", total);
        summary.put("tongMatHang", tonList.size());
        return summary;
    }

    public List<TonKho> searchTonKho(String keyword) {
        return tonKhoRepository.findByTenHangHoaContainingIgnoreCase(keyword);
    }

    public int getTotalInventory() {
        return tonKhoRepository.findAll()
                .stream()
                .mapToInt(TonKho::getSoLuong)
                .sum();
    }

    public List<TonKho> filterByCategory(LoaiHang loaiHang) {
        return tonKhoRepository.findAll()
                .stream()
                .filter(ton -> ton.getLoaiHang().equalsIgnoreCase(loaiHang.name()))
                .toList();
    }

    public List<TonKho> filterTonKho(String loaiHang, String tenHangHoa, Integer minSoLuong, Integer maxSoLuong) {
        return tonKhoRepository.findAll().stream()
                .filter(tk -> loaiHang == null || tk.getLoaiHang().equalsIgnoreCase(loaiHang))
                .filter(tk -> tenHangHoa == null || tk.getTenHangHoa().toLowerCase().contains(tenHangHoa.toLowerCase()))
                .filter(tk -> minSoLuong == null || tk.getSoLuong() >= minSoLuong)
                .filter(tk -> maxSoLuong == null || tk.getSoLuong() <= maxSoLuong)
                .toList();
    }

    // ================== Product ==================
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ================== Product Variant ==================
    public ProductVariant saveVariant(ProductVariant variant) {
        return productVariantRepository.save(variant);
    }

    public List<ProductVariant> getVariants() {
        return productVariantRepository.findAll();
    }

    // ================== Expense ==================

    public Expense addExpense(Expense expense) { return expenseRepository.save(expense); }


    public List<Expense> getExpenses() {
        return expenseRepository.findAll();
    }

    // Lấy danh sách chi phí đang chờ duyệt
    public List<Expense> getPendingExpenses() {
        return expenseRepository.findAll().stream()
                .filter(e -> e.getStatus() == ExpenseStatus.PENDING)
                .toList();
    }

    // DS Đã duyệt
    public Expense approveExpense(Long id) {
        Expense exp = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        exp.setStatus(ExpenseStatus.APPROVED);
        return expenseRepository.save(exp);
    }

    // DS từ chối
    public Expense rejectExpense(Long id) {
        Expense exp = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        exp.setStatus(ExpenseStatus.REJECTED);
        return expenseRepository.save(exp);
    }


    // ================== Schedule ==================
    public Schedule addSchedule(ScheduleRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new RuntimeException("Tiêu đề lịch trình không được để trống!");
        }
        if (request.getDate() == null || request.getDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Ngày lịch trình phải từ hôm nay trở đi!");
        }

        Schedule schedule = new Schedule();
        schedule.setTitle(request.getTitle());
        schedule.setDate(request.getDate());
        schedule.setDescription(
                (request.getDescription() == null || request.getDescription().isEmpty())
                        ? "Chưa cập nhật"
                        : request.getDescription()
        );
        schedule.setStartLocation(
                (request.getStartLocation() == null || request.getStartLocation().isEmpty())
                        ? "Chưa cập nhật"
                        : request.getStartLocation()
        );
        schedule.setEndLocation(
                (request.getEndLocation() == null || request.getEndLocation().isEmpty())
                        ? "Chưa cập nhật"
                        : request.getEndLocation()
        );
        schedule.setScheduleSalary(
                request.getScheduleSalary() != null ? request.getScheduleSalary() : BigDecimal.ZERO
        );
        schedule.setStatus(
                request.getStatus() != null ? ScheduleStatus.valueOf(request.getStatus()) : ScheduleStatus.PENDING
        );
        schedule.setCreatedAt(LocalDateTime.now());
        schedule.setProofDocumentPath("chưa có tài liệu");

        // Lấy truck
        Truck truck = truckRepository.findById(request.getTruckId())
                .orElseThrow(() -> new RuntimeException("Truck not found"));

        // Lấy driver từ truck
        if (truck.getDriver() == null) {
            throw new RuntimeException("Truck chưa được gán tài xế!");
        }

        schedule.setTruck(truck);
        schedule.setDriver(truck.getDriver());

        return scheduleRepository.save(schedule);
    }



    public List<Schedule> getSchedules() {
        return scheduleRepository.findAll();
    }

    public Schedule uploadProof(Long id, MultipartFile file) throws IOException {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch trình!"));

        // Lưu file vào thư mục local
        String uploadDir = "uploads/";
        java.io.File dir = new java.io.File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String filePath = uploadDir + file.getOriginalFilename();
        file.transferTo(new java.io.File(filePath));

        schedule.setProofDocumentPath(filePath);
        return scheduleRepository.save(schedule);
    }

    public Schedule approveSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch trình!"));
        schedule.setStatus(ScheduleStatus.APPROVED);
        return scheduleRepository.save(schedule);
    }

    public Schedule rejectSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch trình!"));
        schedule.setStatus(ScheduleStatus.REJECTED);
        return scheduleRepository.save(schedule);
    }



    // Upload chứng từ
    public String uploadDocument(MultipartFile file) throws IOException {
        try {
            // Tạo entity Document
            Document doc = new Document();
            doc.setFileName(file.getOriginalFilename());
            doc.setContentType(file.getContentType());
            doc.setData(file.getBytes());

            // Lưu file vào DB
            documentRepository.save(doc);
            System.out.println("Đã lưu file vào DB: " + file.getOriginalFilename());

            // Đồng thời lưu file ra thư mục uploads/
            Path path = Paths.get("uploads/" + file.getOriginalFilename());
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            return "Upload file thành công: " + path.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi upload file", e);
        }
    }


    // Submit Schedule
    public Schedule submitSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        // chuyển trạng thái sang PENDING
        schedule.setStatus(ScheduleStatus.PENDING);

        return scheduleRepository.save(schedule);
    }
    public List<PendingScheduleResponse> getPendingSchedules() {
        List<Schedule> schedules = scheduleRepository.findByStatus(ScheduleStatus.PENDING);
        return schedules.stream()
                .map(s -> new PendingScheduleResponse(
                        s.getId(),
                        s.getTitle(),
                        s.getStartLocation(),
                        s.getEndLocation(),
                        s.getStatus().name()
                ))
                .toList();
    }

    // ================== Export Excel ==================
    public ByteArrayInputStream exportExcel() throws IOException {
        List<TonKho> tonKhoList = tonKhoRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TonKho");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Tên Hàng Hóa");
        header.createCell(1).setCellValue("Loại Hàng");
        header.createCell(2).setCellValue("Số Lượng");

        // Data
        int rowIdx = 1;
        for (TonKho ton : tonKhoList) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(ton.getTenHangHoa());
            row.createCell(1).setCellValue(ton.getLoaiHang());
            row.createCell(2).setCellValue(ton.getSoLuong());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    // ================== Import Excel ==================
    public void importExcel(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || row.getCell(0) == null || row.getCell(1) == null || row.getCell(2) == null)
                continue;

            String ten = row.getCell(0).getStringCellValue();
            String loai = row.getCell(1).getStringCellValue();
            int soLuong = (int) row.getCell(2).getNumericCellValue();

            TonKho ton = tonKhoRepository.findByTenHangHoaContainingIgnoreCase(ten)
                    .stream()
                    .findFirst()
                    .orElse(TonKho.builder()
                            .tenHangHoa(ten)
                            .loaiHang(loai)
                            .soLuong(0)
                            .build());

            ton.setSoLuong(ton.getSoLuong() + soLuong); // cộng dồn
            tonKhoRepository.save(ton);
        }
        workbook.close();
    }


// ================== Truck ==================

    // Thêm xe tải
    public TruckResponse addTruck(TruckRequest request) {
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài xế!"));

        Truck truck = new Truck();
        //  KHÔNG set id
        truck.setLicensePlate(request.getLicensePlate());
        truck.setCapacity(request.getCapacity());
        truck.setDriver(driver);
        truck.setStatus(TruckStatus.valueOf(request.getStatus().toUpperCase()));

        Truck saved = truckRepository.save(truck);

        return TruckResponse.builder()
                .id(saved.getId())
                .licensePlate(saved.getLicensePlate())
                .capacity(saved.getCapacity())
                .driverId(saved.getDriver() != null ? saved.getDriver().getId() : null)
                .driverName(saved.getDriver() != null ? saved.getDriver().getFullName() : null)
                .status(saved.getStatus().name())
                .build();

    }


    // Lấy danh sách xe tải
    public List<Truck> getAllTrucks() {
        return truckRepository.findAll();
    }

    // Lấy chi tiết xe tải theo ID
    public Truck getTruckById(Long id) {
        return truckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe tải!"));
    }

    // Cập nhật xe tải
    public TruckResponse updateTruck(Long id, TruckRequest request) {
        Truck existing = truckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe tải!"));

        existing.setLicensePlate(request.getLicensePlate());
        existing.setCapacity(request.getCapacity());
        if (request.getDriverId() != null) {
            Driver driver = driverRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài xế!"));
            existing.setDriver(driver);
        }
        existing.setStatus(TruckStatus.valueOf(request.getStatus().toUpperCase()));

        Truck saved = truckRepository.save(existing);

        return TruckResponse.builder()
                .id(saved.getId())
                .licensePlate(saved.getLicensePlate())
                .capacity(saved.getCapacity())
                .driverId(saved.getDriver() != null ? saved.getDriver().getId() : null)
                .driverName(saved.getDriver() != null ? saved.getDriver().getFullName() : null)
                .status(saved.getStatus().name())
                .build();
    }



    // Xóa xe tải
    public String deleteTruck(Long id) {
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe tải với ID: " + id));

        String truckInfo = String.format("Biển số: %s, Sức chứa: %.2f, Trạng thái: %s",
                truck.getLicensePlate(),
                truck.getCapacity(),
                truck.getStatus());

        truckRepository.delete(truck);

        return "Đã xóa xe tải thành công! Thông tin xe: " + truckInfo;
    }


    //  Cập nhật trạng thái xe tải
    public Truck updateTruckStatus(Long id, String status) {
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe tải!"));

        try {
            TruckStatus newStatus = TruckStatus.valueOf(status.toUpperCase()); // convert String -> Enum
            truck.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + status);
        }

        return truckRepository.save(truck);
    }

    // ================== DRIVER ==================
    // Lấy toàn bộ tài xế
    public List<Driver> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();
        if (drivers.isEmpty()) {
            throw new RuntimeException("Hiện tại chưa có tài xế nào trong hệ thống!");
        }
        return drivers;
    }

    // Lấy tài xế theo ID
    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(" Không tìm thấy tài xế với ID: " + id));
    }

    // Thêm tài xế mới
    public Driver addDriver(Driver driver) {
        driver.setCreatedAt(LocalDate.now());

        if (driver.getStatus() == null) {
            driver.setStatus(DriverStatus.AVAILABLE);
        }

        // Check mã nhân viên trùng
        if (driverRepository.existsByEmployeeCode(driver.getEmployeeCode())) {
            throw new RuntimeException("Mã nhân viên '" + driver.getEmployeeCode() + "' đã tồn tại, vui lòng nhập mã khác!");
        }

        return driverRepository.save(driver);
    }

    // Cập nhật thông tin tài xế
    public Driver updateDriver(Long id, Driver updatedDriver) {
        Driver driver = getDriverById(id);

        driver.setFullName(updatedDriver.getFullName());
        driver.setPhoneNumber(updatedDriver.getPhoneNumber());
        driver.setDateOfBirth(updatedDriver.getDateOfBirth());
        driver.setNote(updatedDriver.getNote());

        if (updatedDriver.getStatus() != null) {
            driver.setStatus(updatedDriver.getStatus());
        }

        return driverRepository.save(driver);
    }

    // Xóa tài xế
    public void deleteDriver(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new RuntimeException("Không thể xóa! Tài xế với ID " + id + " không tồn tại.");
        }
        driverRepository.deleteById(id);
    }

    // Cập nhật trạng thái tài xế
    public Driver updateDriverStatus(Long id, DriverStatus status) {
        Driver driver = getDriverById(id);
        driver.setStatus(status);
        return driverRepository.save(driver);
    }

    // Lấy danh sách tài xế theo trạng thái
    public List<Driver> getDriversByStatus(DriverStatus status) {
        List<Driver> drivers = driverRepository.findByStatus(status);
        if (drivers.isEmpty()) {
            throw new RuntimeException(" Không tìm thấy tài xế nào có trạng thái: " + status);
        }
        return drivers;
    }

// ----------------- EXPENSE TYPE -----------------

    public List<ExpenseType> getAllExpenseTypes() {
        List<ExpenseType> types = expenseTypeRepository.findAll();
        if (types.isEmpty()) {
            throw new RuntimeException("Hiện chưa có loại chi phí nào trong hệ thống!");
        }
        return types;
    }

    public ExpenseType createExpenseType(ExpenseType expenseType) {
        if (expenseType.getName() == null || expenseType.getName().isBlank()) {
            throw new RuntimeException(" Tên loại chi phí không được để trống!");
        }

        return expenseTypeRepository.save(expenseType);
    }

    public ExpenseType updateExpenseType(Long id, ExpenseType expenseType) {
        return expenseTypeRepository.findById(id).map(et -> {
            if (expenseType.getName() == null || expenseType.getName().isBlank()) {
                throw new RuntimeException("Tên loại chi phí không hợp lệ!");
            }
            et.setName(expenseType.getName());
            return expenseTypeRepository.save(et);
        }).orElseThrow(() -> new RuntimeException(" Không tìm thấy loại chi phí với ID: " + id));
    }

    public void deleteExpenseType(Long id) {
        if (!expenseTypeRepository.existsById(id)) {
            throw new RuntimeException(" Không thể xóa! Loại chi phí với ID " + id + " không tồn tại.");
        }
        expenseTypeRepository.deleteById(id);
    }

    // ================== ROUTE ==================
    public List<Route> getAllRoutes() {
        List<Route> routes = routeRepository.findAll();
        if (routes.isEmpty()) {
            throw new RuntimeException("Hiện chưa có tuyến đường nào trong hệ thống!");
        }
        return routes;
    }

    // Get route by code
    public Route getRouteByCode(String routeCode) {
        return routeRepository.findByRouteCode(routeCode)
                .orElseThrow(() -> new RuntimeException(" Không tìm thấy tuyến đường với mã: " + routeCode));
    }

    // Create new route
    public Route createRoute(Route route) {
        if (routeRepository.existsByRouteCode(route.getRouteCode())) {
            throw new RuntimeException(" Mã tuyến '" + route.getRouteCode() + "' đã tồn tại, vui lòng nhập mã khác!");
        }

        if (route.getRouteName() == null || route.getRouteName().isBlank()) {
            throw new RuntimeException("Tên tuyến đường không được để trống!");
        }

        return routeRepository.save(route);
    }

    // Update route
    public Route updateRoute(String routeCode, Route route) {
        Route entity = routeRepository.findByRouteCode(routeCode)
                .orElseThrow(() -> new RuntimeException(" Không tìm thấy tuyến đường với mã: " + routeCode));

        if (route.getRouteName() == null || route.getRouteName().isBlank()) {
            throw new RuntimeException("Tên tuyến đường không hợp lệ!");
        }

        entity.setRouteName(route.getRouteName());
        entity.setTripPrice(route.getTripPrice());
        entity.setNote(route.getNote());

        return routeRepository.save(entity);
    }

    // Delete route
    public void deleteRoute(String routeCode) {
        Route entity = routeRepository.findByRouteCode(routeCode)
                .orElseThrow(() -> new RuntimeException(" Không thể xóa! Tuyến đường với mã " + routeCode + " không tồn tại."));
        routeRepository.delete(entity);
    }


    // ================== SALARY ==================

    // Thêm cấu hình lương cho tài xế
    public SalaryConfig createSalaryConfig(Long driverId, String month, Double baseSalary, Double advance) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        SalaryConfig config = SalaryConfig.builder()
                .driver(driver)
                .month(month)
                .baseSalary(BigDecimal.valueOf(baseSalary))
                .advance(BigDecimal.valueOf(advance))
                .build();

        return salaryConfigRepository.save(config);
    }

    // Thêm lương hiệu suất cho 1 chuyến đi
    public ScheduleSalary createScheduleSalary(Long scheduleId, Long driverId, Double amount) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        ScheduleSalary scheduleSalary = ScheduleSalary.builder()
                .driver(driver)
                .schedule(schedule)
                .amount(BigDecimal.valueOf  (amount))
                .createdAt(LocalDate.now())
                .build();

        return scheduleSalaryRepository.save(scheduleSalary);
    }
}





