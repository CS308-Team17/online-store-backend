package com.example.onlinestore.service;

import com.example.onlinestore.entity.User;
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
import java.util.Optional;

@Service
public class InvoiceService {

    private final String INVOICE_SAVE_PATH = "src/main/resources/invoices/";
    private final String LOGO_PATH = "src/main/resources/logo.jpeg";

    @Autowired
    private EmailService emailService;

    @Autowired
    private FirebaseUserService firebaseUserService;

    public void generateInvoice(String invoiceId) {
        System.out.println("[INFO] Starting invoice generation for invoice ID: " + invoiceId);

        try {
            Map<String, Object> invoiceData = fetchInvoiceData(invoiceId);

            if (isValidInvoiceData(invoiceData)) {
                System.out.println("[INFO] Invoice data is valid.");

                Optional<User> userOptional = fetchCustomerData(invoiceData);

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    String email = user.getEmail();

                    String filePath = createPdfInvoice(invoiceId, invoiceData);
                    sendInvoiceEmail(email, filePath);
                } else {
                    System.err.println("[ERROR] Customer not found for invoice data.");
                }
            } else {
                System.err.println("[ERROR] Invalid invoice data for ID: " + invoiceId);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Error during invoice generation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Map<String, Object> fetchInvoiceData(String invoiceId) throws Exception {
        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> query = firestore.collection("invoices")
                .whereEqualTo("invoiceId", invoiceId)
                .get();

        QuerySnapshot querySnapshot = query.get();
        if (!querySnapshot.isEmpty()) {
            return querySnapshot.getDocuments().get(0).getData();
        } else {
            throw new Exception("No invoice found for ID: " + invoiceId);
        }
    }

    private Optional<User> fetchCustomerData(Map<String, Object> invoiceData) throws Exception {
        String customerId = (String) invoiceData.get("customerId");
        return firebaseUserService.getUserById(customerId);
    }

    private String createPdfInvoice(String invoiceId, Map<String, Object> invoiceData) throws IOException {
        System.out.println("[DEBUG] Starting PDF generation for Invoice ID: " + invoiceId);

        File directory = new File(INVOICE_SAVE_PATH);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create directory: " + INVOICE_SAVE_PATH);
        }

        String filePath = INVOICE_SAVE_PATH + "Invoice_" + invoiceId + ".pdf";
        System.out.println("[DEBUG] PDF will be saved to: " + filePath);

        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(10);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            addLogoToPdf(contentStream, document);
            addInvoiceHeader(contentStream, invoiceId, today.format(formatter), dueDate.format(formatter));
            addCompanyInfo(contentStream);
            addBillingInfo(contentStream, invoiceData);
            addTotalAmount(contentStream, invoiceData);

            contentStream.close();
            document.save(filePath);
        }

        return filePath;
    }

    private void addLogoToPdf(PDPageContentStream contentStream, PDDocument document) throws IOException {
        try {
            PDImageXObject logo = PDImageXObject.createFromFile(LOGO_PATH, document);
            contentStream.drawImage(logo, 400, 750, 100, 50);
        } catch (IOException e) {
            System.err.println("[WARN] Logo not found or failed to load at: " + LOGO_PATH);
        }
    }

    private void addInvoiceHeader(PDPageContentStream contentStream, String invoiceId, String date, String dueDate) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("INVOICE");
        contentStream.endText();

        contentStream.moveTo(50, 740);
        contentStream.lineTo(550, 740);
        contentStream.stroke();

        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(400, 720);
        contentStream.showText("Invoice ID: " + invoiceId);
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Date: " + date);
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Due Date: " + dueDate);
        contentStream.endText();
    }

    private void addCompanyInfo(PDPageContentStream contentStream) throws IOException {
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
    }

    private void addBillingInfo(PDPageContentStream contentStream, Map<String, Object> invoiceData) throws IOException {
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
    }

    private void addTotalAmount(PDPageContentStream contentStream, Map<String, Object> invoiceData) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.setNonStrokingColor(Color.RED);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 500);
        contentStream.showText("TOTAL: $" + String.format("%.2f", invoiceData.get("totalAmount")));
        contentStream.endText();

        contentStream.setNonStrokingColor(Color.BLACK);
    }

    private void sendInvoiceEmail(String email, String filePath) {
        emailService.sendEmailWithAttachment(
                email,
                "Your Invoice",
                "Dear Customer, please find your invoice attached.",
                filePath
        );
    }

    private boolean isValidInvoiceData(Map<String, Object> invoiceData) {
        return invoiceData.containsKey("customerId") && invoiceData.get("customerId") instanceof String &&
                invoiceData.containsKey("orderId") && invoiceData.get("orderId") instanceof String &&
                invoiceData.containsKey("purchaseDate") && invoiceData.get("purchaseDate") instanceof String &&
                invoiceData.containsKey("totalAmount") && invoiceData.get("totalAmount") instanceof Double;
    }
}
