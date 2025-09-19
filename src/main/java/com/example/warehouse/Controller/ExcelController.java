    package com.example.warehouse.Controller;

    import com.example.warehouse.Entity.*;
    import com.example.warehouse.Enum.DriverStatus;
    import com.example.warehouse.Enum.Role;
    import com.example.warehouse.Enum.TransactionType;
    import com.example.warehouse.Enum.TruckStatus;
    import com.example.warehouse.Repository.*;
    import com.example.warehouse.Service.ExcelService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.ByteArrayInputStream;
    import java.util.List;

    @RestController
    @RequestMapping("/excel")
    @RequiredArgsConstructor
    public class ExcelController {

        private final ExcelService excelService;
        private final DriverRepository driverRepository;
        private final TruckRepository truckRepository;
        private final InventoryTransactionRepository inventoryTransactionRepository;
        private final ProductRepository productRepository;
        private final UserRepository userRepository;

        // EXPORT
        @GetMapping("/export")
        public ResponseEntity<byte[]> exportEntity(@RequestParam String entity) throws Exception {
            ByteArrayInputStream in;
            String filename;

            switch (entity.toLowerCase()) {
                case "driver" -> {
                    List<Driver> list = driverRepository.findAll();
                    String[] headers = {"ID", "Name", "Status"};
                    in = excelService.exportToExcel(list, "Drivers", headers,
                            d -> new Object[]{d.getId(), d.getFullName(), d.getStatus()});
                    filename = "drivers.xlsx";
                }
                case "truck" -> {
                    List<Truck> list = truckRepository.findAll();
                    String[] headers = {"ID", "LicensePlate", "Driver", "Status"};
                    in = excelService.exportToExcel(list, "Trucks", headers,
                            t -> new Object[]{t.getId(), t.getLicensePlate(),
                                    t.getDriver() != null ? t.getDriver().getFullName() : "N/A",
                                    t.getStatus()});
                    filename = "trucks.xlsx";
                }
                case "inventorytransaction" -> {
                    List<InventoryTransaction> list = inventoryTransactionRepository.findAll();
                    String[] headers = {"ID", "Product", "Quantity", "Type", "Date"};
                    in = excelService.exportToExcel(list, "InventoryTransaction", headers,
                            it -> new Object[]{it.getId(),
                                    it.getProduct() != null ? it.getProduct().getName() : "N/A",
                                    it.getQuantity(), it.getType(), it.getCreatedAt()});
                    filename = "inventory_transactions.xlsx";
                }
                case "product" -> {
                    List<Product> list = productRepository.findAll();
                    String[] headers = {"ID", "Name", "Category"};
                    in = excelService.exportToExcel(list, "Products", headers,
                            p -> new Object[]{p.getId(), p.getName(), p.getCategory()});
                    filename = "products.xlsx";
                }
                case "user" -> {
                    List<User> list = userRepository.findAll();
                    String[] headers = {"ID", "Username", "Role"};
                    in = excelService.exportToExcel(list, "Users", headers,
                            u -> new Object[]{u.getId(), u.getUsername(), u.getRole()});
                    filename = "users.xlsx";
                }
                default -> throw new RuntimeException("Entity không hỗ trợ export: " + entity);
            }

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Disposition", "attachment; filename=" + filename);
            return ResponseEntity.ok().headers(httpHeaders).body(in.readAllBytes());
        }

        // IMPORT
        @PostMapping("/import")
        public ResponseEntity<String> importEntity(@RequestParam String entity, @RequestParam("file") MultipartFile file) {
            switch (entity.toLowerCase()) {
                case "driver" -> {
                    List<Driver> list = excelService.importFromExcel(file, row -> {
                        try {
                            String code = row.getCell(0).getStringCellValue();
                            String name = row.getCell(1).getStringCellValue();
                            String status = row.getCell(2).getStringCellValue();

                            Driver driver = new Driver();
                            driver.setEmployeeCode(code);
                            driver.setFullName(name);
                            driver.setStatus(DriverStatus.valueOf(status));

                            return driver;
                        } catch (Exception e) {
                            return null;
                        }
                    });
                    driverRepository.saveAll(list);
                    return ResponseEntity.ok("Đã import " + list.size() + " driver");
                }
                case "truck" -> {
                    List<Truck> list = excelService.importFromExcel(file, row -> {
                        try {
                            String plate = row.getCell(1).getStringCellValue();
                            String driverCode = row.getCell(2).getStringCellValue();
                            String status = row.getCell(3).getStringCellValue();

                            Driver driver = driverRepository.findByEmployeeCode(driverCode).orElseGet(() -> {
                                Driver d = new Driver();
                                d.setEmployeeCode(driverCode);
                                d.setFullName("Unknown");
                                d.setStatus(DriverStatus.AVAILABLE);
                                return driverRepository.save(d);
                            });

                            Truck t = new Truck();
                            t.setLicensePlate(plate);
                            t.setDriver(driver);
                            t.setStatus(TruckStatus.valueOf(status.toUpperCase()));
                            return t;
                        } catch (Exception e) {
                            return null;
                        }
                    });
                    truckRepository.saveAll(list);
                    return ResponseEntity.ok("Đã import " + list.size() + " truck");
                }
                case "inventorytransaction" -> {
                    List<InventoryTransaction> list = excelService.importFromExcel(file, row -> {
                        try {
                            String productCode = row.getCell(1).getStringCellValue();
                            int qty = (int) row.getCell(2).getNumericCellValue();
                            String type = row.getCell(3).getStringCellValue();

                            Product product = productRepository.findByCode(productCode).orElse(null);

                            InventoryTransaction it = new InventoryTransaction();
                            it.setProduct(product);
                            it.setQuantity(qty);
                            it.setType(TransactionType.valueOf(type.toUpperCase()));
                            return it;
                        } catch (Exception e) {
                            return null;
                        }
                    });
                    inventoryTransactionRepository.saveAll(list);
                    return ResponseEntity.ok("Đã import " + list.size() + " giao dịch kho");
                }
                case "product" -> {
                    List<Product> list = excelService.importFromExcel(file, row -> {
                        try {
                            String code = row.getCell(1).getStringCellValue();
                            String name = row.getCell(2).getStringCellValue();
                            String category = row.getCell(3).getStringCellValue();
                            int stock = (int) row.getCell(4).getNumericCellValue();

                            Product p = new Product();
                            p.setCode(code);
                            p.setName(name);
                            p.setCategory(category);
                            p.setCurrentStock(stock);

                            return p;
                        } catch (Exception e) {
                            return null;
                        }
                    });
                    productRepository.saveAll(list);
                    return ResponseEntity.ok("Đã import " + list.size() + " product");
                }
                case "user" -> {
                    List<User> list = excelService.importFromExcel(file, row -> {
                        try {
                            String username = row.getCell(1).getStringCellValue();
                            String password = row.getCell(2).getStringCellValue();
                            String roleStr = row.getCell(3).getStringCellValue();

                            User u = new User();
                            u.setUsername(username);
                            u.setPassword(password);
                            u.setRole(Role.valueOf(roleStr.toUpperCase()));

                            return u;
                        } catch (Exception e) {
                            return null;
                        }
                    });
                    userRepository.saveAll(list);
                    return ResponseEntity.ok("Đã import " + list.size() + " user");
                }
                default -> {
                    return ResponseEntity.badRequest().body("Entity không hỗ trợ import: " + entity);
                }
            }
        }
    }
