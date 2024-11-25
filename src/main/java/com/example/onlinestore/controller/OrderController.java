package com.example.onlinestore.controller;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostMapping
    public String saveOrder(@RequestBody Map<String, Object> orderDetails) {
        Firestore db = FirestoreClient.getFirestore();
        db.collection("orders").add(orderDetails);
        return "Order saved successfully";
    }
}
