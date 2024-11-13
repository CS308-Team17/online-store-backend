package com.example.onlinestore.controller;

import com.example.onlinestore.entity.Product;
import com.example.onlinestore.service.FirebaseProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/admin/products")
public class ProductController {

    @Autowired
    private FirebaseProductService firebaseProductService;

    @PostMapping
    public ResponseEntity<String> addProduct(@RequestBody Product product) {
        try {
            String response = firebaseProductService.saveProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }  catch (Exception e) {
            e.printStackTrace();  // Print detailed stack trace in logs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding product: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        try {
            String response = firebaseProductService.deleteProductById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting product");
        }
    }
}
