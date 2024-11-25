package com.example.onlinestore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetails {
    private String orderId;
    private Address address;
    private Payment payment;
    private List<String> productIds;
    private String uid;
}
