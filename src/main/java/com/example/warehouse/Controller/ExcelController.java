    package com.example.warehouse.Controller;

    import com.example.warehouse.Entity.InventoryTransaction;
    import com.example.warehouse.Entity.Product;
    import com.example.warehouse.Enum.Role;
    import com.example.warehouse.Enum.TransactionType;
    import com.example.warehouse.Entity.User;
    import com.example.warehouse.Repository.ProductRepository;
    import com.example.warehouse.Repository.TransactionRepository;
    import com.example.warehouse.Repository.UserRepository;
    import com.example.warehouse.Service.ExcelService;
    import org.apache.poi.ss.usermodel.Row;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.ByteArrayInputStream;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.List;
    import java.util.Objects;
    import java.util.Optional;

    @RestController
    @RequestMapping("/api/warehouse/excel")
    public class ExcelController {

        @Autowired
        private ExcelService excelService;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private TransactionRepository transactionRepository;

        // ========================= IMPORT =========================
        @PostMapping("/import/users")
        public ResponseEntity<String> importUsers(@RequestParam("file") MultipartFile file) {
            try {
                List<User> users = excelService.importFromExcel(file, (Row row) -> {
                    User u = new User();
                    u.setUsername(row.getCell(0).getStringCellValue());
                    u.setPassword(row.getCell(1).getStringCellValue());
                    u.setRole(Role.valueOf(row.getCell(3).getStringCellValue().toUpperCase()));

                    return u;
                });
                userRepository.saveAll(users);
                return ResponseEntity.ok("Imported " + users.size() + " users successfully.");
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Error importing users: " + e.getMessage());
            }
        }

        @PostMapping("/import/transactions")
        public ResponseEntity<String> importTransactions(@RequestParam("file") MultipartFile file) {
            try {
                List<InventoryTransaction> list = excelService.importFromExcel(file, row -> {
                    InventoryTransaction tr = new InventoryTransaction();

                    // Cột chuẩn: 0-ThoiGian, 1-NguoiNhap, 2-GhiChu, 3-Quantity, 4-ProductCode, 5-Type
                    String productCode = getCellValueAsString(row.getCell(4));
                    Product product = productRepository.findByCode(productCode)
                            .orElseThrow(() -> new RuntimeException("Product not found with code: " + productCode));
                    tr.setProduct(product);

                    // Quantity
                    String quantityStr = getCellValueAsString(row.getCell(3));
                    tr.setQuantity(Integer.parseInt(quantityStr));

                    // Type (IMPORT/EXPORT)
                    String typeStr = getCellValueAsString(row.getCell(5));
                    tr.setType(TransactionType.valueOf(typeStr.toUpperCase()));

                    // CreatedAt
                    String createdAtStr = getCellValueAsString(row.getCell(0));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    tr.setCreatedAt(LocalDateTime.parse(createdAtStr, formatter));

                    return tr;
                });

                transactionRepository.saveAll(list);
                return ResponseEntity.ok("Imported " + list.size() + " transactions successfully.");
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Error importing transactions: " + e.getMessage());
            }
        }




        // ========================= EXPORT =========================
            @GetMapping("/export/users")
            public ResponseEntity<byte[]> exportUsers() {
                try {
                    List<User> users = userRepository.findAll();

                    String[] headers = {"ID", "Username", "Password", "Role"};

                    ByteArrayInputStream in = excelService.exportToExcel(
                            users,
                            "Users",     // sheetName
                            headers,     // headers
                            (User u) -> new Object[]{
                                    u.getId(),
                                    u.getUsername(),
                                    u.getPassword(),
                                    u.getRole().name()
                            }
                    );

                    HttpHeaders headersHttp = new HttpHeaders();
                    headersHttp.add("Content-Disposition", "attachment; filename=users.xlsx");

                    return ResponseEntity.ok()
                            .headers(headersHttp)
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .body(in.readAllBytes());
                } catch (Exception e) {
                    return ResponseEntity.status(500).body(null);
                }
            }


        @GetMapping("/export/transactions")
        public ResponseEntity<byte[]> exportTransactions() {
            try {
                List<InventoryTransaction> transactions = transactionRepository.findAll();

                String[] headers = {"ID", "ProductCode", "Quantity", "Type", "CreatedAt"};

                ByteArrayInputStream in = excelService.exportToExcel(
                        transactions,
                        "Transactions",   // sheetName
                        headers,          // header row
                        (InventoryTransaction tr) -> new Object[]{
                                tr.getId(),
                                tr.getProduct() != null ? tr.getProduct().getCode() : "",
                                tr.getQuantity(),
                                tr.getType() != null ? tr.getType().name() : "",
                                tr.getCreatedAt() != null ? tr.getCreatedAt().toString() : ""
                        }
                );

                HttpHeaders headersHttp = new HttpHeaders();
                headersHttp.add("Content-Disposition", "attachment; filename=transactions.xlsx");

                return ResponseEntity.ok()
                        .headers(headersHttp)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(in.readAllBytes());

            } catch (Exception e) {
                return ResponseEntity.status(500)
                        .body(("Error exporting transactions: " + e.getMessage()).getBytes());
            }
        }
        // Helper để đọc giá trị ô Excel an toàn
        private String getCellValueAsString(org.apache.poi.ss.usermodel.Cell cell) {
            if (cell == null) return "";
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();
                case NUMERIC:
                    if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                        return cell.getLocalDateTimeCellValue().toString();
                    } else {
                        double d = cell.getNumericCellValue();
                        if (d == Math.floor(d)) {
                            return String.valueOf((long) d); // số nguyên
                        } else {
                            return String.valueOf(d);        // số thập phân
                        }
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    try {
                        return cell.getStringCellValue();
                    } catch (IllegalStateException e) {
                        return String.valueOf(cell.getNumericCellValue());
                    }
                case BLANK:
                default:
                    return "";
            }
        }

    }
