package com.example.onlinestore.service;

import com.example.onlinestore.Utils.TimeUtils;
import com.example.onlinestore.entity.*;
import com.example.onlinestore.enums.OrderStatus;
import com.example.onlinestore.enums.RefundStatus;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseOrderService {
    private final FirebaseProductService firebaseProductService;
    private final InvoiceService invoiceService;

    @Autowired
    private final FirebaseUserService firebaseUserService;

    @Autowired
    private EmailService emailService;

    public FirebaseOrderService(FirebaseProductService firebaseProductService, InvoiceService invoiceService, FirebaseUserService firebaseUserService) {
        this.firebaseProductService = firebaseProductService;
        this.invoiceService = invoiceService;
        this.firebaseUserService = firebaseUserService;
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

    public boolean cancelOrder(String orderId) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference orderRef = db.collection("orders").document(orderId);
            ApiFuture<WriteResult> writeResult = orderRef.delete();
            writeResult.get(); // Ensure synchronous delete
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to cancel order: " + e.getMessage());
        }
    }

    public boolean requestRefund(RefundRequest refundRequest) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            String refundRequestId = UUID.randomUUID().toString();
            refundRequest.setRefundRequestId(refundRequestId);
            refundRequest.setStatus(RefundStatus.PENDING);
            DocumentReference docRef = db.collection("refundRequests").document(refundRequestId);
            docRef.set(refundRequest).get(); // Ensure synchronous write to Firestore
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to request refund: " + e.getMessage());
        }
    }

    public List<RefundRequest> getAllRefundRequests() {
        try {
            Firestore db = FirestoreClient.getFirestore();
            CollectionReference refundRequestsCollection = db.collection("refundRequests");
            ApiFuture<QuerySnapshot> querySnapshot = refundRequestsCollection.get();
            return querySnapshot.get().toObjects(RefundRequest.class);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to retrieve refund requests: " + e.getMessage());
        }
    }

    public boolean updateRefundStatus(String refundRequestId, RefundStatus status) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference refundRequestRef = db.collection("refundRequests").document(refundRequestId);
            ApiFuture<DocumentSnapshot> future = refundRequestRef.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                RefundRequest refundRequest = document.toObject(RefundRequest.class);
                refundRequest.setStatus(status);
                refundRequestRef.set(refundRequest).get(); // Ensure synchronous write to Firestore

                if (status == RefundStatus.APPROVED) {
                    // Send email to user
                    String userEmail = getUserEmail(refundRequest.getUserId());
                    String subject = "Refund Approved";
                    String text = "Your refund request has been approved. The amount will be refunded to your account.";
                    emailService.sendSimpleMessage(userEmail, subject, text);
                }

                return true;
            } else {
                throw new RuntimeException("Refund request not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update refund status: " + e.getMessage());
        }
    }

    private String getUserEmail(String userId) {
        try {
            // Fetch user details from the user service
            Optional<User> user = firebaseUserService.getUserById(userId);
            return user.get().getEmail().toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user email: " + e.getMessage());
        }
    }
}
