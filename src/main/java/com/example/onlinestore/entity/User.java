package com.example.onlinestore.entity;


import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class User {


    private String userId;
    private String name;
    private String email;
    private String address;


}
