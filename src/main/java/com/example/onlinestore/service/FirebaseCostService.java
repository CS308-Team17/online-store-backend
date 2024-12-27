package com.example.onlinestore.service;

import com.example.onlinestore.Utils.TimeUtils;
import com.example.onlinestore.constants.CollectionConstants;
import com.example.onlinestore.entity.CostDetails;
import com.example.onlinestore.entity.OrderDetails;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FirebaseCostService {
    public CostDetails addCost(double cost) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRef = db.collection(CollectionConstants.COST_COLLECTION).document();
            CostDetails costDetails = new CostDetails(docRef.getId(), TimeUtils.getCurrentDateTimeString(), cost);
            docRef.set(costDetails);
            return costDetails;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add cost: " + e.getMessage());
        }
    }

    public List<CostDetails> getCostDetailsByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            CollectionReference costCollection = db.collection(CollectionConstants.COST_COLLECTION);
            Query query = costCollection.whereGreaterThanOrEqualTo("date", TimeUtils.getDateTimeString(startDate.atStartOfDay()))
                    .whereLessThanOrEqualTo("date", TimeUtils.getDateTimeString(endDate.atTime(23, 59, 59)));
            return getCostDetails(query.get());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get cost details: " + e.getMessage());
        }
    }

    private List<CostDetails> getCostDetails(ApiFuture<QuerySnapshot> querySnapshotApiFuture) throws InterruptedException, java.util.concurrent.ExecutionException {
        List<CostDetails> costs = new ArrayList<>();
        for (DocumentSnapshot document : querySnapshotApiFuture.get().getDocuments()) {
            costs.add(document.toObject(CostDetails.class));
        }
        return costs;
    }
}
