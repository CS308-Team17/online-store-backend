package com.example.onlinestore.service;

import com.example.onlinestore.entity.Invoice;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class InvoiceService {

    private static final String COLLECTION_NAME = "invoices";

    public String saveInvoice(Invoice invoice) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        if (invoice.getInvoiceId() == null || invoice.getInvoiceId().isEmpty()) {
            invoice.setInvoiceId(dbFirestore.collection(COLLECTION_NAME).document().getId());
        }

        dbFirestore.collection(COLLECTION_NAME).document(invoice.getInvoiceId()).set(invoice).get();
        return "Invoice stored successfully with ID: " + invoice.getInvoiceId();
    }

    public Invoice getInvoiceById(String invoiceId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        return dbFirestore.collection(COLLECTION_NAME).document(invoiceId).get().get().toObject(Invoice.class);
    }
}
