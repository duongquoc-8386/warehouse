package com.example.warehouse.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class ExcelService {

    // EXPORT chung
    public <T> ByteArrayInputStream exportToExcel(List<T> data, String sheetName, String[] headers, Function<T, Object[]> mapper) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // Header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < headers.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(headers[col]);
            }

            // Data
            int rowIdx = 1;
            for (T item : data) {
                Row row = sheet.createRow(rowIdx++);
                Object[] values = mapper.apply(item);
                for (int col = 0; col < values.length; col++) {
                    row.createCell(col).setCellValue(values[col] != null ? values[col].toString() : "");
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi export Excel: " + e.getMessage());
        }
    }

    // IMPORT chung
    public <T> List<T> importFromExcel(MultipartFile file, Function<Row, T> rowMapper) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<T> list = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    T item = rowMapper.apply(row);
                    if (item != null) list.add(item);
                }
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi import Excel: " + e.getMessage());
        }
    }
}
