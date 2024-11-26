package com.example.onlinestore.payload;

import com.example.onlinestore.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.annotation.Order;

@Data
@AllArgsConstructor
public class OrderStatusPayload {
    OrderStatus orderStatus;
    String orderId;
}
