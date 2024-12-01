package com.example.onlinestore.service;

import com.example.onlinestore.entity.Product;
import com.example.onlinestore.payload.ProductPayload;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.storage.Acl;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseProductService {

    private static final String COLLECTION_NAME = "products";

    private String uploadImageToStorage(MultipartFile imageFile) {
        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            Blob blob = bucket.create("images/" + imageFile.getOriginalFilename(), imageFile.getBytes(), imageFile.getContentType());
            blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
            return blob.getMediaLink();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    public String saveProduct(ProductPayload productRequest) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference docRef;
        Product product = new Product().setProduct(productRequest);

        List<String> imageUrls = new ArrayList<>();
        if (productRequest.getImages() != null) {
            for (MultipartFile image : productRequest.getImages()) {
                String imageUrl = uploadImageToStorage(image);
                imageUrls.add(imageUrl);
            }
        }
        product.setImageURL(imageUrls);

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
    // Decrease quantity in stock by a given value
    public String decreaseQuantityInStock(String id, int quantity) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Product product = dbFirestore.collection(COLLECTION_NAME).document(id).get().get().toObject(Product.class);
        if (product != null) {
            int newQuantity = product.getQuantityInStock() - quantity;
            if (newQuantity >= 0) {
                dbFirestore.collection(COLLECTION_NAME).document(id).update("quantityInStock", newQuantity).get();
                return "Quantity in stock decreased successfully";
            } else {
                return "Not enough quantity in stock";
            }
        } else {
            return "Product with id " + id + " not found";
        }
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

    public void incrementWishlistCount(String productId) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        dbFirestore.collection(COLLECTION_NAME).document(productId).update("numOfWishlists", com.google.cloud.firestore.FieldValue.increment(1));
    }

    public void decrementWishlistCount(String productId) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        dbFirestore.collection(COLLECTION_NAME).document(productId).update("numOfWishlists", com.google.cloud.firestore.FieldValue.increment(-1));
    }
}
