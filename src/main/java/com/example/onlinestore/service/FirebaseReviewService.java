package com.example.onlinestore.service;

import com.example.onlinestore.entity.Review;
import com.example.onlinestore.enums.ReviewStatus;
import com.example.onlinestore.payload.ReviewPayload;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseReviewService {

    private static final String COLLECTION_NAME = "reviews";
    private final FirebaseUserService firebaseUserService;
    private final FirebaseProductService firebaseProductService;

    // todo find a better way to solve circular dependency than using @Lazy
    public FirebaseReviewService(FirebaseUserService firebaseUserService, @Lazy FirebaseProductService firebaseProductService) {
        this.firebaseUserService = firebaseUserService;
        this.firebaseProductService = firebaseProductService;
    }

    public String saveReview(ReviewPayload reviewPayload) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        // Check if the review rating is in the valid range
        if (reviewPayload.getRating() < 1 || reviewPayload.getRating() > 5) {
            return "Review score must be between 1 and 5";
        }

        //Check if the user exists
        firebaseUserService.getUserById(reviewPayload.getUid());

        //Check if the product exists
        firebaseProductService.getProductById(reviewPayload.getProductId());

        Review review = new Review().setReview(reviewPayload);
        // Generate a document ID if one is not provided
        String documentId = (review.getReviewId() == null || review.getReviewId().trim().isEmpty())
                ? dbFirestore.collection(COLLECTION_NAME).document().getId()
                : review.getReviewId();

        review.setReviewStatus(ReviewStatus.PENDING);
        // Set the ID in the review object
        review.setReviewId(documentId);

        // Save the review to Firestore
        dbFirestore.collection(COLLECTION_NAME).document(documentId).set(review).get();

        return "review added successfully with ID: " + documentId;
    }

    public String deleteReview(String reviewId) throws ExecutionException, InterruptedException{
        Firestore dbFirestore = FirestoreClient.getFirestore();
        dbFirestore.collection(COLLECTION_NAME).document(reviewId).delete();
        return "Review deleted successfully";
    }

    public String setReviewStatus(String reviewId, ReviewStatus statusToChangeTo) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        dbFirestore.collection(COLLECTION_NAME).document(reviewId).update("reviewStatus", statusToChangeTo);
        return "Review status updated successfully";
    }

    public List<Review> getAllReviews() throws  ExecutionException, InterruptedException{
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference reviews = dbFirestore.collection(COLLECTION_NAME);
        return reviews.get().get().toObjects(Review.class);
    }

    public List<Review> getReviewsByProductId(String productId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference reviews = dbFirestore.collection(COLLECTION_NAME);
        return reviews.whereEqualTo("productId", productId).get().get().toObjects(Review.class);
    }

    public List<Review> getReviewsByUserId(String userId) throws ExecutionException, InterruptedException{
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference reviews = dbFirestore.collection(COLLECTION_NAME);
        return reviews.whereEqualTo("uid", userId).get().get().toObjects(Review.class);
    }

    public List<Review> getReviewsByStatus(ReviewStatus status) throws ExecutionException, InterruptedException{
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference reviews = dbFirestore.collection(COLLECTION_NAME);
        return reviews.whereEqualTo("status", status).get().get().toObjects(Review.class);
    }

    public Review getReviewById(String reviewId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        return dbFirestore.collection(COLLECTION_NAME).document(reviewId).get().get().toObject(Review.class);
    }

    public void makeProductNullInReviews(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference reviews = dbFirestore.collection(COLLECTION_NAME);
        reviews.whereEqualTo("productId", id).get().get().toObjects(Review.class).forEach(review -> {
            review.setProductId(null);
            reviews.document(review.getReviewId()).set(review);
        });
    }
}
