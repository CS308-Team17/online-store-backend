package com.example.onlinestore.entity;

import com.example.onlinestore.payload.ProductPayload;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String productId;
    private String name;
    private List<String> imageURL;
    private String model;
    private String serialNumber;
    private String description;
    private int quantityInStock;
    private double price;
    private String warrantyStatus;
    private String distributorInformation;
    private String categoryId;

    // Gets a product request class and sets the values to the product entity
    public Product setProduct(ProductPayload request) {
        this.name = request.getName();
        this.model = request.getModel();
        this.serialNumber = request.getSerialNumber();
        this.description = request.getDescription();
        this.quantityInStock = request.getQuantityInStock();
        this.price = request.getPrice();
        this.warrantyStatus = request.getWarrantyStatus();
        this.distributorInformation = request.getDistributorInformation();
        this.imageURL = new ArrayList<>();
        return this;
    }
}
