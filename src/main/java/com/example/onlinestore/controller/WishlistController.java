package com.example.onlinestore.controller;

import com.example.onlinestore.entity.Wishlist;
import com.example.onlinestore.payload.WishlistPayload;
import com.example.onlinestore.service.FirebaseWishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    private final FirebaseWishlistService wishlistService;

    public WishlistController(FirebaseWishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Wishlist>> getUserWishlist(@PathVariable String userId) throws ExecutionException, InterruptedException, ExecutionException {
        try {
            return ResponseEntity.ok(wishlistService.getUserWishlist(userId));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Object> addToWishlist(@RequestBody WishlistPayload wishlistPayload) throws ExecutionException, InterruptedException {
        try {
            wishlistService.addToWishlist(wishlistPayload);
            return ResponseEntity.ok("Item added to wishlist");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error adding item to wishlist" + e.getMessage());
        }
    }
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<Object> removeFromWishlist(@PathVariable String itemId) throws ExecutionException, InterruptedException {
        try {
            wishlistService.removeFromWishlist(itemId);
            return ResponseEntity.ok("Item removed from wishlist");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error removing item from wishlist" + e.getMessage());
        }
    }
}

