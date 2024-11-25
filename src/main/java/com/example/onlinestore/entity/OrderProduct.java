package com.example.onlinestore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderProduct {
    private String productId;
    private int quantity;
}
