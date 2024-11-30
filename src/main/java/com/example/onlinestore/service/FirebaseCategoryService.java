package com.example.onlinestore.service;

import com.example.onlinestore.entity.Category;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

    @Service
    public class FirebaseCategoryService {

        private static final String COLLECTION_NAME = "categories";

        public String saveCategory(Category category) throws ExecutionException, InterruptedException {
            Firestore dbFirestore = FirestoreClient.getFirestore();

            // Generate a document ID if one is not provided
            String documentId = (category.getId() == null || category.getId().trim().isEmpty())
                    ? dbFirestore.collection(COLLECTION_NAME).document().getId()
                    : category.getId();

            // Set the ID in the category object
            category.setId(documentId);

            // Save the category to Firestore
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


        // Get all categories
        public List<Category> getAll() throws ExecutionException, InterruptedException {
            Firestore dbFirestore = FirestoreClient.getFirestore();
            return dbFirestore.collection(COLLECTION_NAME).get().get().toObjects(Category.class);
        }
    }

