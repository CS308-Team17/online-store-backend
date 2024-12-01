package com.example.onlinestore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Seller {
    private String sellerId;
    private String sellerName;
    private String sellerAddress;
    private String sellerPhone;

    // todo discuss in future do we need different sellers?
    public Seller() {
        this.sellerId = "KitApp";
        this.sellerName = "KitApp";
        this.sellerAddress = "Orta Mahalle, Üniversite Caddesi No:27 Tuzla, 34956 İstanbul";
        this.sellerPhone = "+905425357799";
    }
}

