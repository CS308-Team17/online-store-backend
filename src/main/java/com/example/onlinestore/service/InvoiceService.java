package com.example.onlinestore.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class InvoiceService {

    private final String INVOICE_SAVE_PATH = "src/main/resources/invoices/";
    private final String LOGO_PATH = "src/main/resources/logo.jpeg"; // Path to your logo

    @Autowired
    private EmailService emailService;

    public void generateInvoice(String invoiceId) {
        System.out.println("[INFO] Starting invoice generation for invoice ID: " + invoiceId);

        Firestore firestore = FirestoreClient.getFirestore();
        System.out.println("[DEBUG] Firestore instance initialized: " + (firestore != null));

        ApiFuture<QuerySnapshot> query = firestore.collection("invoices")
                .whereEqualTo("invoiceId", invoiceId)
                .get();

        try {
            QuerySnapshot querySnapshot = query.get();
            if (!querySnapshot.isEmpty()) {
                for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                    System.out.println("[INFO] Found document: " + document.getData());
                    Map<String, Object> invoiceData = document.getData();

                    if (isValidInvoiceData(invoiceData)) {
                        System.out.println("[INFO] Invoice data is valid.");

                        // Fetch customer email
                        String customerId = (String) invoiceData.get("customerId");
                        String email = fetchCustomerEmail(customerId);

                        if (email != null) {
                            // Create PDF and send email
                            createPdfInvoice(invoiceId, invoiceData);
                            emailService.sendEmailWithAttachment(
                                    email,
                                    "Your Invoice",
                                    "Dear Customer, please find your invoice attached.",
                                    INVOICE_SAVE_PATH + "Invoice_" + invoiceId + ".pdf"
                            );
                        } else {
                            System.err.println("[ERROR] Email not found for customer ID: " + customerId);
                        }
                    } else {
                        System.err.println("[ERROR] Invalid invoice data: " + invoiceData);
                    }
                }
            } else {
                System.err.println("[ERROR] No document found with invoiceId: " + invoiceId);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Error fetching invoice data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String fetchCustomerEmail(String customerId) {
        Firestore firestore = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> query = firestore.collection("users")
                .whereEqualTo("uid", customerId)
                .get();

        try {
            QuerySnapshot querySnapshot = query.get();
            if (!querySnapshot.isEmpty()) {
                for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                    return (String) document.get("email");
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Error fetching customer email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private boolean isValidInvoiceData(Map<String, Object> invoiceData) {
        return invoiceData.containsKey("customerId") && invoiceData.get("customerId") instanceof String &&
                invoiceData.containsKey("orderId") && invoiceData.get("orderId") instanceof String &&
                invoiceData.containsKey("purchaseDate") && invoiceData.get("purchaseDate") instanceof String &&
                invoiceData.containsKey("totalAmount") && invoiceData.get("totalAmount") instanceof Double;
    }

    private void createPdfInvoice(String invoiceId, Map<String, Object> invoiceData) throws IOException {
        System.out.println("[DEBUG] Starting PDF generation for Invoice ID: " + invoiceId);

        File directory = new File(INVOICE_SAVE_PATH);
        if (!directory.exists() && !directory.mkdirs()) {
            System.err.println("[ERROR] Failed to create directory: " + INVOICE_SAVE_PATH);
            return;
        }

        String filePath = INVOICE_SAVE_PATH + "Invoice_" + invoiceId + ".pdf";
        System.out.println("[DEBUG] PDF will be saved to: " + filePath);

        // Calculate dynamic due date
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(10);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDueDate = dueDate.format(formatter);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Add logo
            try {
                PDImageXObject logo = PDImageXObject.createFromFile(LOGO_PATH, document);
                contentStream.drawImage(logo, 400, 750, 100, 50);
            } catch (IOException e) {
                System.err.println("[WARN] Logo not found or failed to load at: " + LOGO_PATH);
            }

            // Add title
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("INVOICE");
            contentStream.endText();

            // Draw a line under the title
            contentStream.moveTo(50, 740);
            contentStream.lineTo(550, 740);
            contentStream.stroke();

            // Add company information
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 720);
            contentStream.showText("SWAY");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("123 Business Rd.");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("ISTANBUL");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("TURKEY");
            contentStream.endText();

            // Add invoice details
            contentStream.beginText();
            contentStream.newLineAtOffset(400, 720);
            contentStream.showText("Invoice ID: " + invoiceId);
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Date: " + today.format(formatter));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Due Date: " + formattedDueDate);
            contentStream.endText();

            // Add billing information
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 600);
            contentStream.showText("BILL TO:");
            contentStream.endText();

            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 580);
            contentStream.showText("Customer ID: " + invoiceData.get("customerId"));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Order ID: " + invoiceData.get("orderId"));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Purchase Date: " + invoiceData.get("purchaseDate"));
            contentStream.endText();

            // Add total amount
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.setNonStrokingColor(Color.RED);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 500);
            contentStream.showText("TOTAL: $" + String.format("%.2f", invoiceData.get("totalAmount")));
            contentStream.endText();

            // Reset color to black
            contentStream.setNonStrokingColor(Color.BLACK);

            contentStream.close();
            document.save(filePath);
            System.out.println("[INFO] Invoice PDF saved successfully at: " + filePath);
        }
    }
}
