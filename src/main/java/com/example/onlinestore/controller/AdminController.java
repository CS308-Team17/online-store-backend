package com.example.onlinestore.controller;

import com.example.onlinestore.service.FirebaseUserService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private FirebaseUserService firebaseUserService;

    // Endpoint to assign the ADMIN role
    @PostMapping("/assign-role")
    public ResponseEntity<String> assignAdminRole(@RequestParam String uid) {
        try {
            firebaseUserService.setAdminRole(uid);
            return ResponseEntity.ok("Admin role assigned successfully.");
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error assigning admin role: " + e.getMessage());
        }
    }
}
