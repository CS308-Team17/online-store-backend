package com.example.onlinestore.service;

import com.example.onlinestore.entity.Product;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseProductService {

    private static final String COLLECTION_NAME = "products";

    public String saveProduct(Product product) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference docRef;

        // If product ID is null or empty, Firestore auto-generates an ID
        if (product.getProductId() == null || product.getProductId().trim().isEmpty()) {  // Check for null or empty String
            docRef = dbFirestore.collection(COLLECTION_NAME).document(); // Generate a new document ID
            product.setProductId(docRef.getId()); // Set the generated ID in the product
        } else {
            docRef = dbFirestore.collection(COLLECTION_NAME).document(product.getProductId());
        }

        docRef.set(product).get();
        return "Product added successfully with ID: " + product.getProductId();
    }

    public String deleteProductById(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        dbFirestore.collection(COLLECTION_NAME).document(id).delete().get();
        return "Product deleted successfully";
    }

    public Product getProductById(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        return dbFirestore.collection(COLLECTION_NAME).document(id).get().get().toObject(Product.class);
    }

    public List<Product> getAllProducts() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        List<Product> products = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = dbFirestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            products.add(document.toObject(Product.class));
        }
        return products;
    }
}
