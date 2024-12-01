package com.example.onlinestore.entity;

import com.example.onlinestore.enums.ReviewStatus;
import com.example.onlinestore.payload.ReviewPayload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.onlinestore.Utils.TimeUtils.getCurrentDateTimeString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private String reviewId;
    private String productId;
    private String uid;
    private String name;
    private ReviewStatus reviewStatus;
    private String comment;
    private int rating;
    private String date;

    public Review setReview(ReviewPayload reviewPayload) {
        this.productId = reviewPayload.getProductId();
        this.uid = reviewPayload.getUid();
        this.name = reviewPayload.getName();
        this.comment = reviewPayload.getComment();
        this.rating = reviewPayload.getRating();
        this.date = getCurrentDateTimeString();
        return this;
    }
}
