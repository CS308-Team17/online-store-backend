package com.example.onlinestore.entity;


import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class User {

    private String uid;
    private String name;
    private String email;
    private String address;
    private String role;
}
