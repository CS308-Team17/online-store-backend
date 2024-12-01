package com.example.onlinestore.entity;

import com.example.onlinestore.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetails {
    private String orderId;
    private String uid;
    private String orderDate;
    private OrderStatus orderStatus;
    private double orderTotal;
    private Address address;
    private Payment payment;
    private Seller seller;
    private List<OrderProduct> products;



}




