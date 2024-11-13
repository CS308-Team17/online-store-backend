package com.example.onlinestore.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Product {

    private String productId;           // Ensure this field exists
    private String name;
    private String imageURL;
    private String model;
    private String serialNumber;
    private String description;
    private int quantityInStock;
    private double price;
    private String warrantyStatus;
    private String distributorInformation;

    // Getters and Setters


// other fields, getters, and setters
}
