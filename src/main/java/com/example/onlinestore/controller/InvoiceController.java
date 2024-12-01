package com.example.onlinestore.controller;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;

@RestController
public class InvoiceController {

    private final String INVOICE_SAVE_PATH = "src/main/resources/invoices/";

    @GetMapping("/api/invoices/{orderId}")
    public ResponseEntity<byte[]> getInvoice(@PathVariable String orderId) {
        try {
            String filePath = INVOICE_SAVE_PATH + "Invoice_" + orderId + ".pdf";
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("Invoice not found: " + filePath);
                return ResponseEntity.notFound().build();
            }
    
            byte[] pdfContent = Files.readAllBytes(file.toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("inline").filename("Invoice_" + orderId + ".pdf").build());
    
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}
