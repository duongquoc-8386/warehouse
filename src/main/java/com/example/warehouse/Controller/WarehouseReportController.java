package com.example.warehouse.Controller;

import com.example.warehouse.Dto.Response.*;
import com.example.warehouse.Entity.Driver;
import com.example.warehouse.Entity.SalaryConfig;
import com.example.warehouse.Repository.DriverRepository;
import com.example.warehouse.Repository.SalaryConfigRepository;
import com.example.warehouse.Service.WarehouseReportService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class WarehouseReportController {

    private final SalaryConfigRepository salaryConfigRepository;
    private final DriverRepository driverRepository;
    private final WarehouseReportService reportService;

    // =================== Báo cáo tồn kho ===================
    @GetMapping("/stocks")
    public ResponseEntity<List<StockReportResponse>> getStockReport() {
        return ResponseEntity.ok(reportService.getStockReport());
    }

    // =================== Báo cáo chi phí ===================
    @GetMapping("/expenses")
    public ResponseEntity<List<ExpenseReportResponse>> getExpenseReport() {
        return ResponseEntity.ok(reportService.getExpenseReport());
    }

    // =================== Báo cáo chuyến đi ===================
    @GetMapping("/trips")
    public ResponseEntity<List<TripReportResponse>> getTripReport() {
        return ResponseEntity.ok(reportService.getTripReport());
    }

    // =================== Báo cáo lương theo tháng ===================
    @GetMapping("/salaries")
    public ResponseEntity<List<SalaryReportResponse>> getSalaryReport(@RequestParam String month) {
        return ResponseEntity.ok(reportService.getSalaryReport(month.toUpperCase()));
    }

    // =================== Xuất Excel lương ===================
    @GetMapping("/export/salary")
    public void exportSalaryReport(HttpServletResponse response,
                                   @RequestParam(defaultValue = "#{T(java.time.YearMonth).now().toString()}") String month) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=salary_report_" + month + ".xlsx";
        response.setHeader(headerKey, headerValue);

        // Lấy danh sách Driver bằng bean
        List<Driver> drivers = driverRepository.findAll();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Salary Report");

        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Driver ID", "Driver Name", "Month", "Base Salary", "Advance", "Total Salary"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Data
        int rowNum = 1;
        for (Driver d : drivers) {
            // Lấy salary config theo driver + month
            BigDecimal baseSalary = salaryConfigRepository
                    .findByDriverIdAndMonth(d.getId(), month)
                    .map(SalaryConfig::getBaseSalary)
                    .orElse(BigDecimal.ZERO);

            BigDecimal advance = salaryConfigRepository
                    .findByDriverIdAndMonth(d.getId(), month)
                    .map(SalaryConfig::getAdvance)
                    .orElse(BigDecimal.ZERO);

            BigDecimal totalSalary = baseSalary.subtract(advance);

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getId());
            row.createCell(1).setCellValue(d.getFullName());
            row.createCell(2).setCellValue(month);
            row.createCell(3).setCellValue(baseSalary.doubleValue());
            row.createCell(4).setCellValue(advance.doubleValue());
            row.createCell(5).setCellValue(totalSalary.doubleValue());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
