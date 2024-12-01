package com.example.onlinestore.service;

import com.example.onlinestore.entity.Product;
import com.example.onlinestore.entity.Wishlist;
import com.example.onlinestore.payload.WishlistPayload;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseWishlistService {

    private static final String COLLECTION_NAME = "wishlists";
    private final Firestore firestore;
    private final FirebaseProductService firebaseProductService;;

    public FirebaseWishlistService(FirebaseProductService firebaseProductService) {
        this.firebaseProductService = firebaseProductService;
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
     * @param wishlist Wishlist object containing details to add.
     * @throws ExecutionException   If an error occurs during Firestore call.
     * @throws InterruptedException If the thread is interrupted during Firestore call.
     */
    public String addToWishlist(WishlistPayload wishlistPayload) throws ExecutionException, InterruptedException {

        Product product = firebaseProductService.getProductById(wishlistPayload.getProductId());

        if (isItemInWishlist(wishlistPayload.getUserId(), wishlistPayload.getProductId())) {
            throw new IllegalArgumentException("Wishlist already exists");
        }
        Wishlist wishlist = new Wishlist(wishlistPayload.getUserId(), wishlistPayload.getProductId());

        if (wishlist.getId() == null || wishlist.getId().trim().isEmpty()) {
            String generatedWishlistId= UUID.randomUUID().toString();
            wishlist.setId(generatedWishlistId);
        }

        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(wishlistPayload.getProductId())
                .set(wishlist);

        result.get(); // Ensure operation completion
        return "Item added to wishlist successfully.";
    }

    /**
     * Remove an item from the user's wishlist by item ID.
     *
     * @param itemId ID of the item to remove.
     * @throws ExecutionException   If an error occurs during Firestore call.
     * @throws InterruptedException If the thread is interrupted during Firestore call.
     */
    public String removeFromWishlist(String itemId) throws ExecutionException, InterruptedException {
        if (itemId == null || itemId.isEmpty()) {
            throw new IllegalArgumentException("Item ID must not be null or empty");
        }

        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(itemId)
                .delete();

        result.get(); // Ensure operation completion
        return "Item removed from wishlist successfully.";
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
    public boolean isItemInWishlist(String userId, String itemId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .whereEqualTo("itemId", itemId)
                .get();

        return !query.get().isEmpty();
    }
}
