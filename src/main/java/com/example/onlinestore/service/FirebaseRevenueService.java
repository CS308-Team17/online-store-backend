package com.example.onlinestore.service;

import com.example.onlinestore.Utils.TimeUtils;
import com.example.onlinestore.entity.CostDetails;
import com.example.onlinestore.entity.OrderDetails;
import com.example.onlinestore.entity.RevenueReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseRevenueService {
    @Autowired
    private FirebaseCostService firebaseCostService;
    @Autowired
    private FirebaseOrderService firebaseOrderService;

    public Map<String, RevenueReport> getReport(LocalDate startDate, LocalDate endDate) throws ExecutionException, InterruptedException  {
        Map<String, RevenueReport> dailyReports = new TreeMap<>();

        Map<String, Double> dailyRevenues = getDailyRevenues(startDate, endDate);
        Map<String, Double> dailyCosts = getDailyCosts(startDate, endDate);

        // Iterate over all dates between startDate and endDate (inclusive)
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            String dateKey = getDate(TimeUtils.getDateTimeString(currentDate.atStartOfDay())); // Convert LocalDate to string

            double revenue = dailyRevenues.getOrDefault(dateKey, 0.0);
            double cost = dailyCosts.getOrDefault(dateKey, 0.0);
            double profit = revenue - cost;
            RevenueReport dailyRevenue = new RevenueReport(revenue, cost, profit);

            dailyReports.put(dateKey, dailyRevenue);
            currentDate = currentDate.plusDays(1);
        }

        return dailyReports;
    }

    private Map<String, Double> getDailyRevenues(LocalDate startDate, LocalDate endDate) {
        Map<String, Double> dailyRevenues = new HashMap<>();
        for (OrderDetails order : firebaseOrderService.getOrderDetailsByDateRange(startDate, endDate)) {
            double totalRevenue = order.getOrderTotal();
            dailyRevenues.put(getDate(order.getOrderDate()), dailyRevenues.getOrDefault(order.getOrderDate(), 0.0) + totalRevenue);
        }
        return dailyRevenues;
    }

    private Map<String, Double> getDailyCosts(LocalDate startDate, LocalDate endDate) {
        Map<String, Double> dailyCosts = new HashMap<>();
        for (CostDetails cost : firebaseCostService.getCostDetailsByDateRange(startDate, endDate)) {
            double totalCost = cost.getTotalCost();
            dailyCosts.put(getDate(cost.getDate()), dailyCosts.getOrDefault(cost.getDate(), 0.0) + totalCost);
        }
        return dailyCosts;
    }

    private String getDate(String dateTime) {
        return dateTime.split(" ")[0];
    }
}
