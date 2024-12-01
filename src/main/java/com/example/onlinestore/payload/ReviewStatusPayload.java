package com.example.onlinestore.payload;

import com.example.onlinestore.enums.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewStatusPayload {
    private String reviewId;
    private ReviewStatus status;
}
