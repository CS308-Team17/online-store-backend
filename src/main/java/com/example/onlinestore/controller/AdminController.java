package com.example.onlinestore.controller;

import com.example.onlinestore.entity.RevenueReport;
import com.example.onlinestore.payload.RevenuePayload;
import com.example.onlinestore.service.FirebaseRevenueService;
import com.example.onlinestore.service.FirebaseUserService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private  FirebaseUserService firebaseUserService;
    @Autowired
    private FirebaseRevenueService firebaseRevenueService;


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

    @PostMapping("/revenue-report")
    public ResponseEntity<Object> getRevenueReport(@RequestBody RevenuePayload payload) {
        try {
            // Parse dates into LocalDate
            LocalDate start = LocalDate.parse(payload.getStartDate());
            LocalDate end = LocalDate.parse(payload.getEndDate());

            // Call FirebaseRevenueService to get the report
            return ResponseEntity.ok(firebaseRevenueService.getReport(start, end));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching revenue report");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format: " + e.getMessage());
        }
    }
}
