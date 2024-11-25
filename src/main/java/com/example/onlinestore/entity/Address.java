package com.example.onlinestore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String address;
    private String city;
    private String country;
    private String firstName;
    private String lastName;
    private String state;
    private String zip;
}
