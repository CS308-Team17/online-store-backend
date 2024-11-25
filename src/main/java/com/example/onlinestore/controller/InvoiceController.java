package com.example.onlinestore.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.onlinestore.service.InvoiceService;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/generate")
    public String generateInvoice(@RequestParam String invoiceId) {
        try {
            System.out.println("[INFO] Received request to generate invoice for ID: " + invoiceId);
            invoiceService.generateInvoice(invoiceId);
            return "Invoice generation initiated successfully!";
        } catch (Exception e) {
            System.err.println("[ERROR] Error while generating invoice: " + e.getMessage());
            e.printStackTrace();
            return "Error while generating invoice: " + e.getMessage();
        }
    }

    @GetMapping("/test")
    public String testEndpoint() {
        System.out.println("[INFO] Test endpoint çalıştı.");
        return "Invoice Controller is working!";
    }
}
