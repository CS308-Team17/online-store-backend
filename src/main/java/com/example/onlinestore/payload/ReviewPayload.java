package com.example.onlinestore.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewPayload {
    private String productId;
    private String name; // name of user
    private String uid;
    private String comment;
    private int rating;
}
