package com.example.onlinestore.service;

import com.example.onlinestore.Utils.TimeUtils;
import com.example.onlinestore.entity.OrderDetails;
import com.example.onlinestore.entity.OrderProduct;
import com.example.onlinestore.entity.Seller;
import com.example.onlinestore.enums.OrderStatus;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FirebaseOrderService {
    private final FirebaseProductService firebaseProductService;
    private final InvoiceService invoiceService;

    public FirebaseOrderService(FirebaseProductService firebaseProductService, InvoiceService invoiceService) {
        this.firebaseProductService = firebaseProductService;
        this.invoiceService = invoiceService;
    }

    public ResponseEntity<String> createOrder(OrderDetails orderDetails) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            // Generate an order ID if not provided
            if (orderDetails.getOrderId() == null || orderDetails.getOrderId().trim().isEmpty()) {
                String generatedOrderId = UUID.randomUUID().toString();
                orderDetails.setOrderId(generatedOrderId);
            }
            Seller seller = new Seller();
            orderDetails.setSeller(seller);
            orderDetails.setOrderDate(TimeUtils.getCurrentDateTimeString());
            orderDetails.setOrderStatus(OrderStatus.PROCESSING);

            // Decrease the stock of the products in the order
            for (OrderProduct product : orderDetails.getProducts()) {
                firebaseProductService.decreaseQuantityInStock(product.getProductId(), product.getQuantity());
            }

            // Save the order using the specified or generated order ID
            DocumentReference docRef = db.collection("orders").document(orderDetails.getOrderId());
            docRef.set(orderDetails).get(); // Ensure synchronous write to Firestore

            invoiceService.generateInvoiceFromOrder(orderDetails);

            return ResponseEntity.ok(orderDetails.getOrderId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }
    }


    public List<OrderDetails> getOrdersByUserId(String userId) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            CollectionReference ordersCollection = db.collection("orders");
            Query query = ordersCollection.whereEqualTo("uid", userId);
            return getOrderDetails(query.get(), ordersCollection);
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

    public List<OrderDetails> getAllOrders() {
        try {
            Firestore db = FirestoreClient.getFirestore();
            CollectionReference ordersCollection = db.collection("orders");
            return getOrderDetails(ordersCollection.get(), ordersCollection);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve orders: " + e.getMessage());
        }
    }

    private List<OrderDetails> getOrderDetails(ApiFuture<QuerySnapshot> querySnapshotApiFuture, CollectionReference ordersCollection) throws InterruptedException, java.util.concurrent.ExecutionException {
        List<OrderDetails> orders = new ArrayList<>();
        for (DocumentSnapshot document : querySnapshotApiFuture.get().getDocuments()) {
            orders.add(document.toObject(OrderDetails.class));
        }
        return orders;
    }


    public String updateOrderStatus(String orderId, OrderStatus orderStatus) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference orderRef = db.collection("orders").document(orderId);
            ApiFuture<DocumentSnapshot> future = orderRef.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                OrderDetails orderDetails = document.toObject(OrderDetails.class);
                Objects.requireNonNull(orderDetails).setOrderStatus(orderStatus);
                orderRef.set(orderDetails).get(); // Ensure synchronous write to Firestore
                return "Order status updated successfully";
            } else {
                throw new RuntimeException("Order not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update order status: " + e.getMessage());
        }
    }
}
