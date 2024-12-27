package com.example.onlinestore.service;

import com.example.onlinestore.Utils.TimeUtils;
import com.example.onlinestore.constants.CollectionConstants;
import com.example.onlinestore.entity.CostDetails;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FirebaseCostService {
    public CostDetails addCost(double cost) {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(CollectionConstants.COST_COLLECTION).document();
        CostDetails costDetails = new CostDetails(docRef.getId(), TimeUtils.getCurrentDateTimeString(), cost);
        docRef.set(costDetails);
        return costDetails;
    }
}
