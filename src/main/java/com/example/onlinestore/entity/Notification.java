package com.example.onlinestore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private String id;          // Firestore document ID
    private String uid;         // ID of the user receiving the notification
    private String title;       // Notification title
    private String message;     // Notification message
    private String orderId;     // Associated order ID
    private String status;      // Read/Unread status
    private String timestamp;   // Timestamp in "yyyy-MM-dd HH:mm:ss" format
}
