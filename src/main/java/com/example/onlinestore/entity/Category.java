package com.example.onlinestore.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    private String id;  // Use a String ID compatible with Firebase document IDs

    private String name;
    private String description;

    private List<Product> products;  // Store related products directly as a list
}
