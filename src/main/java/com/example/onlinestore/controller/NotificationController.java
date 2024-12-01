package com.example.onlinestore.controller;

import com.example.onlinestore.entity.Notification;
import com.example.onlinestore.service.NotificationService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.cloud.firestore.Query;

import java.util.List;
import java.util.ArrayList;


import java.util.Collections;


@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/create")
    public String createNotification(@RequestParam String userId,
                                     @RequestParam String title,
                                     @RequestParam String message,
                                     @RequestParam String orderId) {
        notificationService.createNotification(userId, title, message, orderId);
        return "Notification created successfully!";
    }

    @PostMapping("/push")
    public String sendPushNotification(@RequestParam String userId, @RequestParam String message) {
        notificationService.sendPushNotification(userId, message);
        return "Push notification sent!";
    }

    @GetMapping("/api/notifications/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable String userId) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            // Fetch notifications for the user from Firestore
            ApiFuture<QuerySnapshot> querySnapshot = db.collection("notifications")
                    .whereEqualTo("userId", userId)
                    .orderBy("timestamp", Query.Direction.DESCENDING) // Sort by most recent first
                    .get();

            // Parse documents into Notification objects
            List<Notification> notifications = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                Notification notification = document.toObject(Notification.class);
                if (notification != null) {
                    notification.setId(document.getId()); // Set Firestore document ID
                    notifications.add(notification);
                }
            }

            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            // Log and return error response
            System.err.println("[ERROR] Failed to fetch notifications: " + e.getMessage());
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }
}