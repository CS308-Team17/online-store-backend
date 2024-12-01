package com.example.onlinestore.service;

import com.example.onlinestore.entity.Notification;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.text.SimpleDateFormat;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    private static final String COLLECTION_NAME = "notifications";

    public void createNotification(String userId, String title, String message, String orderId) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            Notification notification = new Notification();
            notification.setUid(userId);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setOrderId(orderId);
            notification.setStatus("unread");
            notification.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            db.collection(COLLECTION_NAME).add(notification);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create notification: " + e.getMessage());
        }
    }

    public void sendPushNotification(String userId, String message) {
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, message);
    }
}
