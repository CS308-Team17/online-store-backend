package com.example.onlinestore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueReport {
    private double totalRevenue;
    // Todo: Discuss profit field (currently cost data unavailable)
    // private double profit;
}
