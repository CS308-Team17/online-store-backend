package com.example.onlinestore.service;

import com.example.onlinestore.entity.Product;
import com.example.onlinestore.entity.User;
import com.example.onlinestore.entity.Wishlist;
import com.example.onlinestore.payload.WishlistPayload;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseWishlistService {

    private static final String COLLECTION_NAME = "wishlists";
    private final Firestore firestore;
    private final FirebaseProductService firebaseProductService;;
    private final FirebaseUserService firebaseUserService;


    public FirebaseWishlistService(FirebaseProductService firebaseProductService, FirebaseUserService firebaseUserService) {
        this.firebaseProductService = firebaseProductService;
        this.firebaseUserService = firebaseUserService;
        this.firestore = FirestoreClient.getFirestore(); // Initialize Firestore from Firebase
    }

    /**
     * Retrieve the wishlist for a specific user.
     *
     * @param userId ID of the user.
     * @return List of Wishlist items for the user.
     * @throws ExecutionException   If an error occurs during Firestore call.
     * @throws InterruptedException If the thread is interrupted during Firestore call.
     */
    public List<Wishlist> getUserWishlist(String userId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .get();

        return query.get().toObjects(Wishlist.class); // Automatically map to Wishlist class
    }

    /**
     * Add a new item to the user's wishlist.
     *
     * @param wishlistPayload Wishlist object containing details to add.
     * @throws ExecutionException   If an error occurs during Firestore call.
     * @throws InterruptedException If the thread is interrupted during Firestore call.
     */
    public String addToWishlist(WishlistPayload wishlistPayload) throws ExecutionException, InterruptedException {
        // Check if product exists, if not throws exception
        firebaseProductService.getProductById(wishlistPayload.getProductId());

        // Check if the user exists
        Optional<User> user = firebaseUserService.getUserById(wishlistPayload.getUserId());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User does not exist");
        }

        if (isItemInWishlist(wishlistPayload.getUserId(), wishlistPayload.getProductId())) {
            throw new IllegalArgumentException("Wishlist already exists");
        }

        // Create the wishlist entry
        Wishlist wishlist = new Wishlist(wishlistPayload.getUserId(), wishlistPayload.getProductId());
        String generatedWishlistId = wishlist.getId() == null || wishlist.getId().trim().isEmpty()
                ? UUID.randomUUID().toString()
                : wishlist.getId();
        wishlist.setId(generatedWishlistId);

        // Add the item to Firestore
        try {
            ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                    .document(wishlist.getId()) // Use the unique wishlist ID
                    .set(wishlist);
            result.get(); // Ensure operation completes
        } catch (Exception e) {
            throw new IllegalStateException("Failed to add item to wishlist", e);
        }

        // Increment the wishlist count for the product
        firebaseProductService.incrementWishlistCount(wishlistPayload.getProductId());

        return String.format("Item added to wishlist successfully. Wishlist ID: %s", wishlist.getId());
    }



    /**
     * Remove an item from the user's wishlist by item ID.
     *
     * @param itemId ID of the item to remove.
     * @throws ExecutionException   If an error occurs during Firestore call.
     * @throws InterruptedException If the thread is interrupted during Firestore call.
     */
    public void removeFromWishlist(String itemId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(itemId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            Wishlist wishlist = document.toObject(Wishlist.class);

            // Remove the item from Firestore
            firestore.collection(COLLECTION_NAME).document(itemId).delete().get();

            // Decrement the wishlist count for the product
            firebaseProductService.decrementWishlistCount(wishlist.getProductId());
        } else {
            throw new IllegalArgumentException("Item does not exist in wishlist");
        }
    }

    /**
     * Check if an item is already in the user's wishlist.
     *
     * @param userId ID of the user.
     * @param itemId ID of the item.
     * @return True if the item exists in the user's wishlist, otherwise false.
     * @throws ExecutionException   If an error occurs during Firestore call.
     * @throws InterruptedException If the thread is interrupted during Firestore call.
     */
    private boolean isItemInWishlist(String userId, String itemId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .whereEqualTo("productId", itemId)
                .get();

        return !query.get().isEmpty();

    }
}
