package com.example.onlinestore.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String model;
    private String serialNumber;
    private String description;
    private int quantityInStock;
    private double price;
    private String warrantyStatus;
    private String distributorInformation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // Getters and Setters
}
