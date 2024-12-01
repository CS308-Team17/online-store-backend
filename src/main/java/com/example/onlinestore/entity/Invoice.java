package com.example.onlinestore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Invoice {

        private String invoiceId;        // Unique ID for the invoice
        private String orderId;          // Associated order ID
        private String customerId;       // Customer who made the order
        private double totalAmount;      // Total price
        private String purchaseDate;
        private Date date;
        private List<String> productDetails; // e.g., Product names or IDs// Date of purchase




}
