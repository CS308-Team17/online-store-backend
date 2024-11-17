package com.example.onlinestore.controller;

import com.example.onlinestore.entity.User;
import com.example.onlinestore.service.FirebaseUserService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private FirebaseUserService firebaseUserService;

    /**
     * Creates or updates a user in Firestore.
     *
     * @param user The user data to be saved.
     * @return Success message with the user's ID.
     */
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
            String response = firebaseUserService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating user: " + e.getMessage());
        }
    }

    /**
     * Retrieves a user by ID from Firestore.
     *
     * @param id The user ID.
     * @return The user data if found, or a 404 Not Found status.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        try {
            Optional<User> user = firebaseUserService.getUserById(id);
            return user.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Assigns the 'ADMIN' role to a user.
     *
     * @param uid The Firebase UID of the user.
     * @return Success message or error message.
     */
    @PostMapping("/{uid}/role/admin")
    public ResponseEntity<String> assignAdminRole(@PathVariable String uid) {
        try {
            firebaseUserService.setAdminRole(uid);
            return ResponseEntity.ok("Admin role assigned to user with UID: " + uid);
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error assigning admin role: " + e.getMessage());
        }
    }

    /**
     * Assigns the 'CUSTOMER' role to a user.
     *
     * @param uid The Firebase UID of the user.
     * @return Success message or error message.
     */
    @PostMapping("/{uid}/role/customer")
    public ResponseEntity<String> assignCustomerRole(@PathVariable String uid) {
        try {
            firebaseUserService.setCustomerRole(uid);
            return ResponseEntity.ok("Customer role assigned to user with UID: " + uid);
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error assigning customer role: " + e.getMessage());
        }
    }

    /**
     * Checks if a user has the 'ADMIN' role.
     *
     * @param uid The Firebase UID of the user.
     * @return True if the user has the 'ADMIN' role, otherwise false.
     */
    @GetMapping("/{uid}/isAdmin")
    public ResponseEntity<Boolean> isAdmin(@PathVariable String uid) {
        try {
            boolean isAdmin = firebaseUserService.isAdmin(uid);
            return ResponseEntity.ok(isAdmin);
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Checks if a user has the 'CUSTOMER' role.
     *
     * @param uid The Firebase UID of the user.
     * @return True if the user has the 'CUSTOMER' role, otherwise false.
     */
    @GetMapping("/{uid}/isCustomer")
    public ResponseEntity<Boolean> isCustomer(@PathVariable String uid) {
        try {
            boolean isCustomer = firebaseUserService.isCustomer(uid);
            return ResponseEntity.ok(isCustomer);
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Removes all roles from a user.
     *
     * @param uid The Firebase UID of the user.
     * @return Success message or error message.
     */
    @DeleteMapping("/{uid}/role")
    public ResponseEntity<String> removeUserRole(@PathVariable String uid) {
        try {
            firebaseUserService.removeUserRole(uid);
            return ResponseEntity.ok("Roles removed for user with UID: " + uid);
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing roles: " + e.getMessage());
        }
    }

    /**
     * Deletes a user by ID from Firestore.
     *
     * @param id The user ID.
     * @return Success message or error message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable String id) {
        try {
            String response = firebaseUserService.deleteUserById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user");
        }
    }
}
