package com.example.onlinestore.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class InvoiceService {

    private final String INVOICE_SAVE_PATH = "src/main/resources/invoices/";
    private final String LOGO_PATH = "src/main/resources/logo.jpeg"; // Path to your logo

    public void generateInvoice(String invoiceId) {
        System.out.println("[INFO] Starting invoice generation for invoice ID: " + invoiceId);

        Firestore firestore = FirestoreClient.getFirestore(); // Get Firestore instance

        ApiFuture<DocumentSnapshot> future = firestore.collection("invoices").document(invoiceId).get();

        try {
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                System.out.println("[INFO] Invoice document found for ID: " + invoiceId);
                @SuppressWarnings("unchecked")
                Map<String, Object> invoiceData = document.getData();

                if (isValidInvoiceData(invoiceData)) {
                    createPdfInvoice(invoiceId, invoiceData);
                } else {
                    System.err.println("[ERROR] Invoice data is missing required fields or has invalid types.");
                }
            } else {
                System.err.println("[ERROR] Invoice not found for ID: " + invoiceId);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Error fetching invoice data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidInvoiceData(Map<String, Object> invoiceData) {
        return invoiceData.containsKey("customerId") && invoiceData.get("customerId") instanceof String &&
               invoiceData.containsKey("orderId") && invoiceData.get("orderId") instanceof String &&
               invoiceData.containsKey("purchaseDate") && invoiceData.get("purchaseDate") instanceof String &&
               invoiceData.containsKey("totalAmount") && invoiceData.get("totalAmount") instanceof Double;
    }

    private void createPdfInvoice(String invoiceId, Map<String, Object> invoiceData) throws IOException {
        File directory = new File(INVOICE_SAVE_PATH);
        if (!directory.exists() && !directory.mkdirs()) {
            System.err.println("[ERROR] Failed to create directory: " + INVOICE_SAVE_PATH);
            return;
        }

        String filePath = INVOICE_SAVE_PATH + "Invoice_" + invoiceId + ".pdf";

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Add logo
            try {
                PDImageXObject logo = PDImageXObject.createFromFile(LOGO_PATH, document);
                contentStream.drawImage(logo, 50, 720, 100, 50);
            } catch (IOException e) {
                System.err.println("[WARN] Logo not found at: " + LOGO_PATH);
            }

            // Add title
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
            contentStream.beginText();
            contentStream.newLineAtOffset(200, 750);
            contentStream.showText("Invoice");
            contentStream.endText();

            // Add invoice details
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 680);
            contentStream.showText("Invoice ID: " + invoiceId);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Customer ID: " + invoiceData.get("customerId"));
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Order ID: " + invoiceData.get("orderId"));
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Purchase Date: " + invoiceData.get("purchaseDate"));
            contentStream.newLineAtOffset(0, -20);
            double totalAmount = ((Double) invoiceData.get("totalAmount"));
            contentStream.showText("Total Amount: $" + String.format("%.2f", totalAmount));
            contentStream.endText();

            contentStream.close();

            document.save(filePath);
            System.out.println("[INFO] Invoice saved to: " + filePath);
        }
    }
}
