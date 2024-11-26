package com.example.onlinestore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProduct {
    private String productId;
    private int quantity;
    private String imageURL;
    private double price;
    private String name;
}
