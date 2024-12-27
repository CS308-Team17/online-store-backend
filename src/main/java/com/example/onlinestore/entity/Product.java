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
    // General product information
    private String productId;
    private String name;
    private String description;
    private List<String> imageURL;
    private double price;
    private int quantityInStock;
    private String categoryId;
    private int numOfWishlists;

    // Distributor and model-related information
    private String serialNumber;
    private String distributorInformation;
    private String warrantyStatus;

    // Book-specific information
    private String author;
    private String publisher;
    private String isbn;
    private String language;
    private int numberOfPages;
    private String publicationDate;
    private String edition;

    // Discount-related fields
    private double discountPercentage; 
    private Double previousPrice; 
    private double originalPrice;


    // Method to set product details from the payload
    public Product setProduct(ProductPayload request) {
        // General product information
        this.name = request.getName();
        this.description = request.getDescription();
        this.quantityInStock = request.getQuantityInStock();
        this.price = request.getPrice();
        this.categoryId = request.getCategoryId();
        this.numOfWishlists = 0;

        // Distributor and model-related information
        this.serialNumber = request.getSerialNumber();
        this.distributorInformation = request.getDistributorInformation();
        this.warrantyStatus = request.getWarrantyStatus();

        // Book-specific information
        this.author = request.getAuthor();
        this.publisher = request.getPublisher();
        this.isbn = request.getIsbn();
        this.language = request.getLanguage();
        this.numberOfPages = request.getNumberOfPages();
        this.publicationDate = request.getPublicationDate();
        this.edition = request.getEdition();
        this.imageURL = new ArrayList<>();
        return this;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }
    
    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public void applyDiscount(double discountPercentage) {
        if (discountPercentage > 0 && discountPercentage <= 100) {
            this.previousPrice = this.price; 
            this.price = this.price * (1 - discountPercentage / 100);
            this.discountPercentage = discountPercentage;
        }
    }

    public void removeDiscount() {
        if (this.previousPrice != null) {
            this.price = this.previousPrice; 
            this.previousPrice = null;
            this.discountPercentage = 0;
        }
    }
}
