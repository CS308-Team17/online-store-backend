package com.example.onlinestore.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class InvoiceController {

    private final String INVOICE_SAVE_PATH = "src/main/resources/invoices/";

    @GetMapping("/api/invoices/{orderId}")
    public ResponseEntity<byte[]> getInvoice(@PathVariable String orderId) {
        try {
            String filePath = INVOICE_SAVE_PATH + "Invoice_" + orderId + ".pdf";
            File file = new File(filePath);
            if (!file.exists()) {
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

    @GetMapping("/api/invoices")
    public ResponseEntity<List<Map<String, String>>> getInvoices(@RequestParam(required = false) String date) {
        try {
            File folder = new File(INVOICE_SAVE_PATH);
            File[] files = folder.listFiles((dir, name) -> name.startsWith("Invoice_") && name.endsWith(".pdf"));

            if (files == null || files.length == 0) {
                return ResponseEntity.noContent().build();
            }

            List<Map<String, String>> invoices = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (File file : files) {
                String fileName = file.getName();
                String orderId = fileName.substring(8, fileName.length() - 4);

                FileTime fileTime = Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime();
                String fileDate = dateFormat.format(fileTime.toMillis());

                if (date == null || date.equals(fileDate)) {
                    Map<String, String> invoiceData = new HashMap<>();
                    invoiceData.put("orderId", orderId);
                    invoiceData.put("date", fileDate);
                    invoiceData.put("fileName", fileName);
                    invoices.add(invoiceData);
                }
            }

            if (invoices.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
