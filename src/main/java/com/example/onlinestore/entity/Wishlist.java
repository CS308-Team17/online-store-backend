package com.example.onlinestore.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {

    private String id;
    private String userId;
    private String productId;

    public Wishlist(String userId, String productId) {
        this.userId = userId;
        this.productId = productId;
    }

}
