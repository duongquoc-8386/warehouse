package com.example.warehouse.Service;

import com.example.warehouse.Entity.TonKho;
import com.example.warehouse.Repository.TonKhoRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {

    private final TonKhoRepository tonKhoRepository;

    public ExcelService(TonKhoRepository tonKhoRepository) {
        this.tonKhoRepository = tonKhoRepository;
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
}
