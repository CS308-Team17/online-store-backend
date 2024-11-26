package com.example.onlinestore.controller;
import com.example.onlinestore.entity.OrderDetails;
import com.example.onlinestore.payload.OrderStatusPayload;
import com.example.onlinestore.service.FirebaseOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final FirebaseOrderService firebaseOrderService;

    public OrderController(FirebaseOrderService firebaseOrderService) {
        this.firebaseOrderService = firebaseOrderService;
    }

    @PostMapping("/create")
    public String createOrder(@RequestBody OrderDetails orderDetails) {
        return firebaseOrderService.createOrder(orderDetails);
    }

    @GetMapping("/{orderId}")
    public OrderDetails getOrderById(@PathVariable String orderId) {
        return firebaseOrderService.getOrderById(orderId);
    }

    @GetMapping("/getByUserId/{userId}")
    public List<OrderDetails> getOrdersByUserId(@PathVariable String userId) {
        return firebaseOrderService.getOrdersByUserId(userId);
    }

    @GetMapping("/getAll")
    public List<OrderDetails> getAllOrders() {
        return firebaseOrderService.getAllOrders();
    }

    @PutMapping("/updateStatus")
    public String updateOrderStatus(@RequestBody OrderStatusPayload orderStatusPayload) {
        return firebaseOrderService.updateOrderStatus(orderStatusPayload.getOrderId(), orderStatusPayload.getOrderStatus());
    }

}
