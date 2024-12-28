package com.example.onlinestore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueReport {
    private Map<String, Double> revenueByDate;
    private Map<String, Double> costByDate;
    private Map<String, Double> profitByDate;
    private double totalRevenue;
    private double totalCost;
    private double totalProfit;
}
