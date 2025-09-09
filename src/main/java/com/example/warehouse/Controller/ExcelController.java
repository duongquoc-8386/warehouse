package com.example.warehouse.Controller;

import com.example.warehouse.Service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/warehouse/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    // ================== Export Excel ==================
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportExcel() throws IOException {
        ByteArrayInputStream in = excelService.exportExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=tonkho.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(in));
    }

    // ================== Import Excel ==================
    @PostMapping("/import")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        excelService.importExcel(file);
        return ResponseEntity.ok("Import thành công!");
    }
}
