package com.example.onlinestore.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenuePayload {
    private String startDate;
    private String endDate;
}
