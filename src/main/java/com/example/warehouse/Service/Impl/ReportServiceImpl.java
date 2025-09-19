    package com.example.warehouse.Service.Impl;

    import com.example.warehouse.Dto.Response.*;
    import com.example.warehouse.Entity.Driver;
    import com.example.warehouse.Entity.Expense;
    import com.example.warehouse.Entity.Trip;
    import com.example.warehouse.Entity.Truck;
    import com.example.warehouse.Enum.ExpenseStatus;
    import com.example.warehouse.Repository.DriverRepository;
    import com.example.warehouse.Repository.ExpenseRepository;
    import com.example.warehouse.Repository.TripRepository;
    import com.example.warehouse.Repository.TruckRepository;
    import com.example.warehouse.Service.ReportService;
    import org.springframework.stereotype.Service;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.stream.Collectors;

    @Service
    public class ReportServiceImpl implements ReportService {

        private final DriverRepository driverRepository;
        private final TruckRepository truckRepository;
        private final TripRepository tripRepository;
        private final ExpenseRepository expenseRepository;

        public ReportServiceImpl(DriverRepository driverRepository,
                                 TruckRepository truckRepository,
                                 TripRepository tripRepository,
                                 ExpenseRepository expenseRepository) {
            this.driverRepository = driverRepository;
            this.truckRepository = truckRepository;
            this.tripRepository = tripRepository;
            this.expenseRepository = expenseRepository;
        }

        // ---------- Báo cáo Lương ----------
        @Override
        public DriverReportResponse getSalaryReportAllByDriver(Long driverId) {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));

            DriverReportResponse response = new DriverReportResponse();
            response.setDriverId(driver.getId());
            response.setDriverName(driver.getFullName());
            response.setBasicSalary(5000000.0);   // lương cứng
            response.setAdvancePayment(1000000.0); // tiền ứng

            // Tính performanceSalary từ Trip (dùng cost thay cho scheduleSalary)
            List<Trip> trips = tripRepository.findByDriverId(driverId);

            List<DriverSalaryDetail> details = trips.stream().map(t -> {
                DriverSalaryDetail detail = new DriverSalaryDetail();
                detail.setTripId(t.getId());
                detail.setRouteSalary(t.getCost() != null ? t.getCost() : 0.0);
                detail.setCost(0.0); // placeholder nếu có thêm chi phí khác
                detail.setDescription(t.getDescription());
                return detail;
            }).collect(Collectors.toList());

            response.setDetails(details);

            // Tính tổng performanceSalary
            double performanceSalary = details.stream().mapToDouble(DriverSalaryDetail::getRouteSalary).sum();
            response.setPerformanceSalary(performanceSalary);

            // Tính tổng lương
            double totalSalary = response.getBasicSalary() + performanceSalary - response.getAdvancePayment();
            response.setTotalSalary(totalSalary);

            return response;
        }

        // ---------- Báo cáo Chi phí ----------
        @Override
        public VehicleCostResponse getVehicleCostReport(Long truckId) {
            Truck truck = truckRepository.findById(truckId)
                    .orElseThrow(() -> new RuntimeException("Truck not found"));

            List<Trip> trips = tripRepository.findByTruckId(truckId);
            List<Expense> expenses = expenseRepository.findByTruckIdAndStatus(truckId, ExpenseStatus.APPROVED);

            List<VehicleCostDetail> details = new ArrayList<>();
            double totalCost = 0;

            for (Trip t : trips) {
                VehicleCostDetail detail = new VehicleCostDetail();
                detail.setTripId(t.getId());
                detail.setCost(t.getCost() != null ? t.getCost() : 0.0);
                detail.setDescription(t.getDescription());
                totalCost += detail.getCost();
                details.add(detail);
            }

            for (Expense e : expenses) {
                VehicleCostDetail detail = new VehicleCostDetail();
                detail.setTripId(null);
                detail.setCost(e.getAmount() != null ? e.getAmount() : 0.0);
                detail.setDescription(e.getType() + ": " + e.getDescription());
                totalCost += detail.getCost();
                details.add(detail);
            }

            VehicleCostResponse response = new VehicleCostResponse();
            response.setTruckId(truck.getId());
            response.setTruckCode(truck.getLicensePlate());
            response.setTotalCost(totalCost);
            response.setDetails(details);

            return response;
        }

        @Override
        public VehicleCostSummaryResponse getVehicleCostAll() {
            List<Truck> trucks = truckRepository.findAll();
            List<VehicleCostResponse> vehicleCosts = new ArrayList<>();
            double totalCostAllTrucks = 0;

            for (Truck truck : trucks) {
                VehicleCostResponse vcr = getVehicleCostReport(truck.getId());
                vehicleCosts.add(vcr);
                totalCostAllTrucks += vcr.getTotalCost();
            }

            VehicleCostSummaryResponse summary = new VehicleCostSummaryResponse();
            summary.setVehicleCosts(vehicleCosts);
            summary.setTotalCostAllTrucks(totalCostAllTrucks);
            return summary;
        }

        // ---------- Báo cáo Lịch trình ----------
        @Override
        public List<TripDetailResponse> getScheduleDetailsAllByTruck(Long truckId) {
            List<Trip> trips = tripRepository.findByTruckId(truckId);
            List<TripDetailResponse> responses = new ArrayList<>();

            for (Trip t : trips) {
                TripDetailResponse response = new TripDetailResponse();
                response.setTripId(t.getId()); // đổi sang tripId
                response.setTruckCode(t.getTruck() != null ? t.getTruck().getLicensePlate() : "");
                response.setDriverName(t.getDriver() != null ? t.getDriver().getFullName() : "");
                response.setDescription(t.getDescription());
                response.setCost(t.getCost() != null ? t.getCost() : 0.0);
                response.setDocumentUrl(t.getProofDocumentPath() != null ? t.getProofDocumentPath() : "");
                responses.add(response);
            }
            return responses;
        }

        @Override
        public AllScheduleDetailResponse getScheduleDetailsAll() {
            List<Trip> trips = tripRepository.findAll();
            List<TripDetailResponse> responses = new ArrayList<>();
            double totalCostAllTrucks = 0;

            for (Trip t : trips) {
                TripDetailResponse response = new TripDetailResponse();
                response.setTripId(t.getId());
                response.setTruckCode(t.getTruck() != null ? t.getTruck().getLicensePlate() : "");
                response.setDriverName(t.getDriver() != null ? t.getDriver().getFullName() : "");
                response.setDescription(t.getDescription());
                response.setCost(t.getCost() != null ? t.getCost() : 0.0);
                response.setDocumentUrl(t.getProofDocumentPath() != null ? t.getProofDocumentPath() : "");
                totalCostAllTrucks += response.getCost();
                responses.add(response);
            }

            AllScheduleDetailResponse summary = new AllScheduleDetailResponse();
            summary.setSchedules(responses);
            summary.setTotalCostAllTrucks(totalCostAllTrucks);
            return summary;
        }
    }
