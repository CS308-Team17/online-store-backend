package com.example.onlinestore.service;

import com.example.onlinestore.constants.CollectionConstants;
import com.example.onlinestore.constants.FirebaseConstants;
import com.example.onlinestore.entity.OrderDetails;
import com.example.onlinestore.entity.User;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class InvoiceService {


    private final String INVOICE_SAVE_PATH = "src/main/resources/invoices/";
    private final String LOGO_PATH = "src/main/resources/logo.png";

    private final EmailService emailService;

    private final FirebaseUserService firebaseUserService;

    public InvoiceService(EmailService emailService, FirebaseUserService firebaseUserService) {
        this.emailService = emailService;
        this.firebaseUserService = firebaseUserService;
    }

    public void generateInvoicePDF(OrderDetails orderDetails) {
        try {
            Optional<User> user = firebaseUserService.getUserById(orderDetails.getUid());
            if (user.isEmpty()) {
                throw new RuntimeException("User not found for ID: " + orderDetails.getUid());
            }
            String filePath = createPdfInvoice(orderDetails);
            sendInvoiceEmail(user.get().getEmail(), filePath);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateInvoiceFromOrder(OrderDetails orderDetails) {
        try {
            generateInvoicePDF(orderDetails); // Use the existing logic to create the PDF and send it
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating invoice from order: " + e.getMessage());
        }
    }


    private String createPdfInvoice(OrderDetails orderDetails) throws IOException {
        System.out.println("[DEBUG] Starting PDF generation for Invoice ID: " + orderDetails.getOrderId());

        File directory = new File(INVOICE_SAVE_PATH);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create directory: " + INVOICE_SAVE_PATH);
        }

        String filePath = INVOICE_SAVE_PATH + "Invoice_" + orderDetails.getOrderId() + ".pdf";
        System.out.println("[DEBUG] PDF will be saved to: " + filePath);

        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(10);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            addLogoToPdf(contentStream, document);
            addInvoiceHeader(contentStream, orderDetails.getOrderId(), today.format(formatter), dueDate.format(formatter));
            addCompanyInfo(contentStream);
            addBillingInfo(contentStream, orderDetails);
            addTotalAmount(contentStream, orderDetails);

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
        contentStream.showText("KitApp");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("123 Business Rd.");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("ISTANBUL");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("TURKEY");
        contentStream.endText();
    }

    private void addBillingInfo(PDPageContentStream contentStream, OrderDetails orderDetails) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 600);
        contentStream.showText("BILL TO:");
        contentStream.endText();

        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 580);
        contentStream.showText("Customer ID: " + orderDetails.getUid());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Order ID: " + orderDetails.getOrderId());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Purchase Date: " +orderDetails.getOrderDate());
        contentStream.endText();
    }

    private void addTotalAmount(PDPageContentStream contentStream, OrderDetails orderDetails) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.setNonStrokingColor(Color.RED);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 500);
        contentStream.showText("TOTAL: $" + String.format("%.2f", orderDetails.getOrderTotal()));
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
}