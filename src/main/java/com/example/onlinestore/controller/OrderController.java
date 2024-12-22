package com.example.onlinestore.controller;
import com.example.onlinestore.entity.OrderDetails;
import com.example.onlinestore.entity.RefundRequest;
import com.example.onlinestore.payload.OrderStatusPayload;
import com.example.onlinestore.service.FirebaseOrderService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> createOrder(@RequestBody OrderDetails orderDetails) {
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

    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable String orderId) {
        boolean isCancelled = firebaseOrderService.cancelOrder(orderId);
        if (isCancelled) {
            return ResponseEntity.ok("Order cancelled successfully");
        } else {
            return ResponseEntity.status(404).body("Order not found");
        }
    }

    @PostMapping("/requestRefund")
    public ResponseEntity<String> requestRefund(@RequestBody RefundRequest refundRequest) {
        boolean isRequested = firebaseOrderService.requestRefund(refundRequest);
        if (isRequested) {
            return ResponseEntity.ok("Refund request submitted successfully");
        } else {
            return ResponseEntity.status(500).body("Failed to submit refund request");
        }
    }

    @GetMapping("/refundRequests")
    public List<RefundRequest> getAllRefundRequests() {
        return firebaseOrderService.getAllRefundRequests();
    }

    @PutMapping("/updateRefundStatus")
    public ResponseEntity<String> updateRefundStatus(@RequestBody RefundRequest refundRequest) {
        try {
            boolean isUpdated = firebaseOrderService.updateRefundStatus(refundRequest.getRefundRequestId(), refundRequest.getStatus());
            if (isUpdated) {
                return ResponseEntity.ok("Refund status updated successfully");
            } else {
                return ResponseEntity.status(500).body("Failed to update refund status");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update refund status: " + e.getMessage());
        }
    }

}
