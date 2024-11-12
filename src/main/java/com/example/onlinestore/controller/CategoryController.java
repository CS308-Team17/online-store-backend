package com.example.onlinestore.controller;

import com.example.onlinestore.entity.Category;
import com.example.onlinestore.service.FirebaseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final FirebaseCategoryService firebaseCategoryService;

    @Autowired
    public CategoryController(FirebaseCategoryService firebaseCategoryService) {
        this.firebaseCategoryService = firebaseCategoryService;
    }

    // Endpoint to save a new category
    @PostMapping
    public ResponseEntity<String> addCategory(@RequestBody Category category) {
        try {
            String response = firebaseCategoryService.saveCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding category");
        }
    }

    // Endpoint to delete a category by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable String id) {
        try {
            String response = firebaseCategoryService.deleteCategoryById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting category");
        }
    }

    // Endpoint to get a category by ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable String id) {
        try {
            Category category = firebaseCategoryService.getCategoryById(id);
            if (category != null) {
                return ResponseEntity.ok(category);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
