package com.example.onlinestore.service;

import com.example.onlinestore.entity.OrderDetails;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FirebaseOrderService {
    public String createOrder(OrderDetails orderDetails) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            // Generate an order ID if not provided
            if (orderDetails.getOrderId() == null || orderDetails.getOrderId().trim().isEmpty()) {
                String generatedOrderId = UUID.randomUUID().toString();
                orderDetails.setOrderId(generatedOrderId);
            }

            // Save the order using the specified or generated order ID
            DocumentReference docRef = db.collection("orders").document(orderDetails.getOrderId());
            docRef.set(orderDetails).get(); // Ensure synchronous write to Firestore

            return "Order saved successfully with ID: " + orderDetails.getOrderId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save the order: " + e.getMessage());
        }
    }


    public List<OrderDetails> getOrdersByUserId(String userId) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            CollectionReference ordersCollection = db.collection("orders");
            Query query = ordersCollection.whereEqualTo("uid", userId);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<OrderDetails> orders = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                orders.add(document.toObject(OrderDetails.class));
            }
            return orders;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve orders: " + e.getMessage());
        }
    }

    public OrderDetails getOrderById(String orderId) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference orderRef = db.collection("orders").document(orderId);
            ApiFuture<DocumentSnapshot> future = orderRef.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                return document.toObject(OrderDetails.class);
            } else {
                throw new RuntimeException("Order not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve order: " + e.getMessage());
        }
    }
}
