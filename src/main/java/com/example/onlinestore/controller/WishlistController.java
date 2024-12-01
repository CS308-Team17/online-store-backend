package com.example.onlinestore.controller;

import com.example.onlinestore.entity.Wishlist;
import com.example.onlinestore.payload.WishlistPayload;
import com.example.onlinestore.service.FirebaseWishlistService;
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
    public List<Wishlist> getUserWishlist(@PathVariable String userId) throws ExecutionException, InterruptedException, ExecutionException {
        return wishlistService.getUserWishlist(userId);
    }

    @PostMapping
    public void addToWishlist(@RequestBody WishlistPayload wishlistPayload) throws ExecutionException, InterruptedException {
        wishlistService.addToWishlist(wishlistPayload);
    }

    @DeleteMapping("/remove/{itemId}")
    public void removeFromWishlist(@PathVariable String itemId) throws ExecutionException, InterruptedException {
        wishlistService.removeFromWishlist(itemId);
    }
}

