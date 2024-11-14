package com.example.onlinestore.controller;

import com.example.onlinestore.entity.Product;
import com.example.onlinestore.request.ProductRequest;
import com.example.onlinestore.service.FirebaseProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/admin/products")
public class ProductController {

    @Autowired
    private FirebaseProductService firebaseProductService;

    //Get all products
    @GetMapping("getAll")
    public ResponseEntity<Object> getAllProducts() {
        try {
            return ResponseEntity.ok(firebaseProductService.getAllProducts());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching products");
        }
    }
    //Get product by ID
    @GetMapping("getById/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable String id) {
        try {
            Product product = firebaseProductService.getProductById(id);
            if (product != null) {
                return ResponseEntity.ok(product);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching product");
        }
    }

    @PostMapping
    public ResponseEntity<String> addProduct(@ModelAttribute ProductRequest request) {
        try {
            String response = firebaseProductService.saveProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }  catch (Exception e) {
            e.printStackTrace();  // Print detailed stack trace in logs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding product: " + e.getMessage());
        }
    }
    // Add multiple
    @PostMapping("/addMultiple")
    public ResponseEntity<String> addMultipleProducts(@ModelAttribute ProductRequest[] requests) {
        try {
            for (ProductRequest request : requests) {
                firebaseProductService.saveProduct(request);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Products added successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding products: " + e.getMessage());
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
