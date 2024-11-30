package com.example.onlinestore.payload;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPayload {

    private String name;
    private String serialNumber;
    private String description;
    private int quantityInStock;
    private double price;
    private String warrantyStatus;
    private String distributorInformation;
    private List<MultipartFile> images;
    private String categoryId;

    // Book-specific fields
    private String author;
    private String publisher;
    private String isbn;
    private String language;
    private int numberOfPages;
    private String publicationDate;
    private String edition;

}
