package com.example.onlinestore.service;

import com.example.onlinestore.entity.Category;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

    @Service
    public class FirebaseCategoryService {

        private static final String COLLECTION_NAME = "categories";

        public String saveCategory(Category category) throws ExecutionException, InterruptedException {
            Firestore dbFirestore = FirestoreClient.getFirestore();
            String documentId = category.getId() != null ? category.getId() : dbFirestore.collection(COLLECTION_NAME).document().getId();
            dbFirestore.collection(COLLECTION_NAME).document(documentId).set(category).get();
            return "Category added successfully with ID: " + documentId;
        }

        // Delete a category by ID
        public String deleteCategoryById(String id) throws ExecutionException, InterruptedException {
            Firestore dbFirestore = FirestoreClient.getFirestore();
            dbFirestore.collection(COLLECTION_NAME).document(id).delete().get();
            return "Category deleted successfully";
        }

        // Find a category by ID
        public Category getCategoryById(String id) throws ExecutionException, InterruptedException {
            Firestore dbFirestore = FirestoreClient.getFirestore();
            return dbFirestore.collection(COLLECTION_NAME).document(id).get().get().toObject(Category.class);
        }
    }

