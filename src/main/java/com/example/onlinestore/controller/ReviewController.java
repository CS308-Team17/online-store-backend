package com.example.onlinestore.controller;


import com.example.onlinestore.enums.ReviewStatus;
import com.example.onlinestore.payload.ReviewPayload;
import com.example.onlinestore.payload.ReviewStatusPayload;
import com.example.onlinestore.service.FirebaseReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/review")
public class ReviewController {

    private final FirebaseReviewService firebaseReviewService;

    public ReviewController(FirebaseReviewService firebaseReviewService) {
        this.firebaseReviewService = firebaseReviewService;
    }

    // Add review
   @PostMapping("/add")
    public ResponseEntity<Object> addReview(@RequestBody ReviewPayload reviewPayload) {
       try {
           return ResponseEntity.ok(firebaseReviewService.saveReview(reviewPayload));
       } catch (ExecutionException | InterruptedException e) {
           e.printStackTrace();
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching review");
       }
    }

    // Delete review
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<Object> deleteReview(@PathVariable String reviewId) {
        try {
            return ResponseEntity.ok(firebaseReviewService.deleteReview(reviewId));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching reviews" + e.getMessage());
        }
    }

    // Set review status
    @PutMapping("/setStatus")
    public ResponseEntity<Object> setReviewStatus(@RequestBody ReviewStatusPayload reviewStatusPayload) {
        try {
            return ResponseEntity.ok(firebaseReviewService.setReviewStatus(reviewStatusPayload.getReviewId(), reviewStatusPayload.getStatus()));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching reviews" + e.getMessage());
        }
    }

    // Approve review
    @PutMapping("/approve/{reviewId}")
    public ResponseEntity<Object> approveReview(@PathVariable String reviewId) {
        try {
            return ResponseEntity.ok(firebaseReviewService.setReviewStatus(reviewId, ReviewStatus.APPROVED));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching reviews" + e.getMessage());
        }
    }

    // Decline review
    @PutMapping("/decline/{reviewId}")
    public ResponseEntity<Object> declineReview(@PathVariable String reviewId) {
        try {
            return ResponseEntity.ok(firebaseReviewService.setReviewStatus(reviewId, ReviewStatus.DECLINED));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching products" + e.getMessage());
        }
    }

    // Get all reviews
    @GetMapping("/getAll")
    public ResponseEntity<Object> getAllReviews() {
        try {
            return ResponseEntity.ok(firebaseReviewService.getAllReviews());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching products" + e.getMessage());
        }
    }

    @GetMapping("/getAllPending")
    public ResponseEntity<Object> getPendingReviews() {
        try {
            return ResponseEntity.ok(firebaseReviewService.getReviewsByStatus(ReviewStatus.PENDING));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching products" + e.getMessage());
        }
    }

    // Get reviews by product ID
    @GetMapping("/getByProductId/{productId}")
    public ResponseEntity<Object> getReviewsByProductId(@PathVariable String productId) {
        try {
            return ResponseEntity.ok(firebaseReviewService.getReviewsByProductId(productId));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching reviews" + e.getMessage());
        }
    }

    // Get reviews by user ID
    @GetMapping("/getByUserId/{userId}")
    public ResponseEntity<Object> getReviewsByUserId(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(firebaseReviewService.getReviewsByUserId(userId));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching reviews" + e.getMessage());
        }
    }

    // Get reviews by status
    @GetMapping("/getByStatus/{status}")
    public ResponseEntity<Object> getReviewsByStatus(@PathVariable ReviewStatus status) {
        try {
            return ResponseEntity.ok(firebaseReviewService.getReviewsByStatus(status));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching reviews" + e.getMessage());
        }
    }

    // Get reviews by review id
    @GetMapping("/getById/{reviewId}")
    public ResponseEntity<Object> getReviewById(@PathVariable String reviewId) {
        try {
            return ResponseEntity.ok(firebaseReviewService.getReviewById(reviewId));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching reviews" + e.getMessage());
        }
    }


}
