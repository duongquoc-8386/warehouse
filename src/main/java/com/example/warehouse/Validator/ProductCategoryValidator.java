package com.example.warehouse.Validator;

import com.example.warehouse.Enum.LoaiHang;
import java.util.HashMap;
import java.util.Map;

public class ProductCategoryValidator {

    private static final Map<String, LoaiHang> productCategoryMap = new HashMap<>();

    static {
        // ánh xạ Từ khóa chính -> Loại hàng
        productCategoryMap.put("Laptop", LoaiHang.DIEN_TU);
        productCategoryMap.put("CPU", LoaiHang.DIEN_TU);
        productCategoryMap.put("FAN", LoaiHang.DIEN_TU);
        productCategoryMap.put("Ram", LoaiHang.DIEN_TU);
        productCategoryMap.put("Quạt Fan", LoaiHang.DIEN_TU);
        productCategoryMap.put("Ghế Gaming", LoaiHang.NOI_THAT);
    }

    public static boolean isValid(String tenHang, LoaiHang loaiHang) {
        return productCategoryMap.entrySet().stream()
                .anyMatch(entry ->
                        tenHang.toLowerCase().contains(entry.getKey().toLowerCase()) &&
                                entry.getValue() == loaiHang
                );
    }
}
