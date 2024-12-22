package com.example.onlinestore.service;

import com.example.onlinestore.constants.CollectionConstants;
import com.example.onlinestore.entity.OrderDetails;
import com.example.onlinestore.entity.Product;
import com.example.onlinestore.entity.RevenueReport;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseRevenueService {
    private final Firestore firestore;

    public FirebaseRevenueService() {
        this.firestore = FirestoreClient.getFirestore();
    }

    public RevenueReport getReport(LocalDate startDate, LocalDate endDate) throws ExecutionException, InterruptedException {
        CollectionReference orders = firestore.collection(CollectionConstants.ORDER_COLLECTION);

        // Convert LocalDate to the "yyyy/MM/dd HH:mm:ss" format
        String start = startDate.atStartOfDay().format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        String end = endDate.atTime(23, 59, 59).format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        // Query Firestore with the formatted date strings
        ApiFuture<QuerySnapshot> query = orders
                .whereGreaterThanOrEqualTo("orderDate", start)
                .whereLessThanOrEqualTo("orderDate", end)
                .get();

        List<QueryDocumentSnapshot> documents = query.get().getDocuments();
        double totalRevenue = 0;

        for (QueryDocumentSnapshot document : documents) {
            double orderTotal = document.toObject(OrderDetails.class).getOrderTotal();
            totalRevenue += orderTotal;
        }

        return new RevenueReport(totalRevenue);
    }
}
