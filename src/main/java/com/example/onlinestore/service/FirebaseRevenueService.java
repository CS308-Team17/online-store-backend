package com.example.onlinestore.service;

import com.example.onlinestore.constants.CollectionConstants;
import com.example.onlinestore.entity.CostDetails;
import com.example.onlinestore.entity.OrderDetails;
import com.example.onlinestore.entity.RevenueReport;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class FirebaseRevenueService {
    @Autowired
    private FirebaseCostService firebaseCostService;
    @Autowired
    private FirebaseOrderService firebaseOrderService;

    public RevenueReport getReport(LocalDate startDate, LocalDate endDate) throws ExecutionException, InterruptedException {
        Map<String, Double> revenueByDate = getRevenueByDate(startDate, endDate);
        Map<String, Double> costByDate = getCostByDate(startDate, endDate);
        Map<String, Double> profitByDate = getProfitByDate(revenueByDate, costByDate);
        return new RevenueReport(revenueByDate, costByDate, profitByDate, calculateTotal(revenueByDate), calculateTotal(costByDate), calculateTotal(profitByDate));
    }

    private Map<String, Double> getRevenueByDate(LocalDate startDate, LocalDate endDate) {
        List<OrderDetails> orders = firebaseOrderService.getOrderDetailsByDateRange(startDate, endDate);
        return orders.stream().collect(Collectors.groupingBy(OrderDetails::getOrderDate, Collectors.summingDouble(OrderDetails::getOrderTotal)));
    }

    private Map<String, Double> getCostByDate(LocalDate startDate, LocalDate endDate) {
        List<CostDetails> costs = firebaseCostService.getCostDetailsByDateRange(startDate, endDate);
        return costs.stream().collect(Collectors.groupingBy(CostDetails::getDate, Collectors.summingDouble(CostDetails::getTotalCost)));
    }

    private Map<String, Double> getProfitByDate(Map<String, Double> revenueByDate, Map<String, Double> costByDate) {
        return revenueByDate.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() - costByDate.getOrDefault(entry.getKey(), 0.0)));
    }

    private double calculateTotal(Map<String, Double> map) {
        return map.values().stream().mapToDouble(Double::doubleValue).sum();
    }
}
