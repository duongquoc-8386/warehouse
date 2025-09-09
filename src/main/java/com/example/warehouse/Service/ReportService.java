package com.example.warehouse.Service;

import com.example.warehouse.Dto.Request.*;
import com.example.warehouse.Dto.Response.*;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ReportService {

    // Báo cáo lương tổng hợp
    List<SalarySummaryResponse> getSalarySummary(SalaryRequest request);

    //  Báo cáo chi tiết lương
    List<SalaryDetailResponse> getSalaryDetail(SalaryRequest request);

    // Cấu hình lương
    void updateSalaryConfig(SalaryConfigRequest request);

    //  Báo cáo chi phí 1 xe
    List<ExpenseResponse> getTruckExpenses(ReportFilterRequest request);

    //  Báo cáo chi phí tất cả xe
    List<TruckExpenseSummaryResponse> getAllTruckExpenses(ReportFilterRequest request);
}
