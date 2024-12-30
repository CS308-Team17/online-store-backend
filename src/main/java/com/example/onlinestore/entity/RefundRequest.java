package com.example.onlinestore.entity;

import com.example.onlinestore.enums.RefundStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RefundRequest {
    private String refundRequestId;
    private String orderId;
    private String productId;
    private String productName;
    private String userId;
    private String userEmail;
    private String reason;
    private String orderDate;
    private RefundStatus status;
}