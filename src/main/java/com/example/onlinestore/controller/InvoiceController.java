package com.example.onlinestore.controller;

import com.example.onlinestore.entity.Invoice;
import com.example.onlinestore.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /**
     * Generate a PDF invoice for a given invoice ID.
     */
    @PostMapping("/{invoiceId}/generate")
    public ResponseEntity<String> generateInvoice(@PathVariable String invoiceId) {
        try {
            invoiceService.generateInvoice(invoiceId);
            return ResponseEntity.ok("Invoice successfully generated.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating invoice: " + e.getMessage());
        }
    }

    /**
     * Generate a new invoice based on order data.
     */
    @PostMapping("/generate-from-order")
    public ResponseEntity<String> generateInvoiceFromOrder(@RequestParam String orderId) {
        try {
            invoiceService.generateInvoiceFromOrder(orderId);
            return ResponseEntity.ok("Invoice successfully generated from order.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating invoice from order: " + e.getMessage());
        }
    }


/*
    @GetMapping("/{invoiceId}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable String invoiceId) {
        try {
            // Assuming getInvoiceById method is added to return a mapped Invoice object
            Invoice invoice = invoiceService.getInvoiceById(invoiceId);
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }*/
}
