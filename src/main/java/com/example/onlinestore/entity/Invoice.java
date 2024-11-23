package com.example.onlinestore.entity;

import lombok.Data;

@Data
public class Invoice {
    private String invoiceId;        // Unique ID for the invoice
    private String orderId;          // Associated order ID
    private String customerId;       // Customer who made the order
    private double totalAmount;      // Total price
    private String purchaseDate;     // Date of purchase
}
