package com.example.onlinestore.Service;

import com.example.onlinestore.entity.OrderDetails;
import com.example.onlinestore.enums.OrderStatus;
import com.example.onlinestore.service.FirebaseOrderService;
import com.example.onlinestore.service.FirebaseProductService;
import com.example.onlinestore.service.InvoiceService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.cloud.storage.Cors.Origin.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FirebaseOrderServiceTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private ApiFuture<WriteResult> writeResultFuture;

    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotFuture;

    @Mock
    private ApiFuture<DocumentSnapshot> documentSnapshotFuture;

    @Mock
    private FirebaseProductService firebaseProductService;

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private FirebaseOrderService firebaseOrderService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(anyString())).thenReturn(documentReference);
    }

    @Test
    public void getOrderById_ShouldReturnOrderDetails() throws Exception {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            // Mock Firestore collection and document retrieval
            when(firestore.collection("orders")).thenReturn(collectionReference);
            when(collectionReference.document("123")).thenReturn(documentReference);

            // Mock Firestore get operation
            DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);

            // Mock existing order details
            OrderDetails mockOrderDetails = new OrderDetails();
            when(documentSnapshot.exists()).thenReturn(true);
            when(documentSnapshot.toObject(OrderDetails.class)).thenReturn(mockOrderDetails);

            // Call the method and validate
            OrderDetails result = firebaseOrderService.getOrderById("123");

            assertNotNull(result);
            assertEquals(mockOrderDetails, result);
        }
    }


    @Test
    public void getOrderById_ShouldThrowExceptionIfNotFound() throws Exception {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            // Mock Firestore collection and document retrieval
            when(firestore.collection("orders")).thenReturn(collectionReference);
            when(collectionReference.document("123")).thenReturn(documentReference);

            // Mock Firestore get operation
            DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);

            // Mock document does not exist
            when(documentSnapshot.exists()).thenReturn(false);

            // Call the method and validate the exception
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    firebaseOrderService.getOrderById("123")
            );

            assertTrue(exception.getMessage().contains("Order not found"));
        }
    }




    @Test
    public void getAllOrders_ShouldReturnOrderList() throws Exception {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            // Mock Firestore collection retrieval
            when(firestore.collection("orders")).thenReturn(collectionReference);

            // Mock Firestore get operation
            QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
            when(collectionReference.get()).thenReturn(querySnapshotFuture);
            when(querySnapshotFuture.get()).thenReturn(querySnapshot);

            // Mock the documents returned by Firestore
            List<QueryDocumentSnapshot> documents = new ArrayList<>();
            QueryDocumentSnapshot documentSnapshot = mock(QueryDocumentSnapshot.class);
            documents.add(documentSnapshot);

            when(querySnapshot.getDocuments()).thenReturn(documents);
            when(documentSnapshot.toObject(OrderDetails.class)).thenReturn(new OrderDetails());

            // Call the method and validate
            List<OrderDetails> orders = firebaseOrderService.getAllOrders();

            assertNotNull(orders);
            assertEquals(1, orders.size());
            verify(querySnapshot).getDocuments();
            verify(documentSnapshot).toObject(OrderDetails.class);
        }
    }



}
