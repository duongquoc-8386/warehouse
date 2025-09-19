package com.example.warehouse.Validator;

import com.example.warehouse.Enum.ProductCategory;
import java.util.HashMap;
import java.util.Map;

public class ProductCategoryValidator {

    private static final Map<String, ProductCategory> productCategoryMap = new HashMap<>();

    static {
        // ánh xạ Từ khóa chính -> Loại hàng
        productCategoryMap.put("Laptop", ProductCategory.DIEN_TU);
        productCategoryMap.put("CPU", ProductCategory.DIEN_TU);
        productCategoryMap.put("FAN", ProductCategory.DIEN_TU);
        productCategoryMap.put("Ram", ProductCategory.DIEN_TU);
        productCategoryMap.put("Quạt Fan", ProductCategory.DIEN_TU);
        productCategoryMap.put("Ghế Gaming", ProductCategory.NOI_THAT);
    }

    public static boolean isValid(String tenHang, ProductCategory productCategory) {
        return productCategoryMap.entrySet().stream()
                .anyMatch(entry ->
                        tenHang.toLowerCase().contains(entry.getKey().toLowerCase()) &&
                                entry.getValue() == productCategory
                );
    }
}
