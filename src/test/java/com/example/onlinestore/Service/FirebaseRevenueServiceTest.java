package com.example.onlinestore.service;

import com.example.onlinestore.constants.CollectionConstants;
import com.example.onlinestore.entity.OrderDetails;
import com.example.onlinestore.entity.RevenueReport;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FirebaseRevenueServiceTest {

    private FirebaseRevenueService firebaseRevenueService;

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private QueryDocumentSnapshot documentSnapshot1;

    @Mock
    private QueryDocumentSnapshot documentSnapshot2;

    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotFuture;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            firebaseRevenueService = new FirebaseRevenueService();
        }
    }

    @Test
    void getReport_ShouldReturnCorrectRevenue() throws ExecutionException, InterruptedException {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        when(firestore.collection(CollectionConstants.ORDER_COLLECTION)).thenReturn(collectionReference);

        CollectionReference query = mock(CollectionReference.class);
        when(collectionReference.whereGreaterThanOrEqualTo(eq("orderDate"), anyString()))
                .thenReturn(query);
        when(query.whereLessThanOrEqualTo(eq("orderDate"), anyString()))
                .thenReturn(query);

        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);

        OrderDetails order1 = new OrderDetails();
        order1.setOrderTotal(100.0);

        OrderDetails order2 = new OrderDetails();
        order2.setOrderTotal(200.0);

        when(documentSnapshot1.toObject(OrderDetails.class)).thenReturn(order1);
        when(documentSnapshot2.toObject(OrderDetails.class)).thenReturn(order2);

        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(documentSnapshot1, documentSnapshot2));

        // Act
        RevenueReport report = firebaseRevenueService.getReport(startDate, endDate);

        // Assert
        assertEquals(300.0, report.getTotalRevenue());
        verify(firestore).collection(CollectionConstants.ORDER_COLLECTION);
        verify(querySnapshotFuture).get();
    }

    @Test
    void getReport_ShouldReturnZero_WhenNoOrders() throws ExecutionException, InterruptedException {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        when(firestore.collection(CollectionConstants.ORDER_COLLECTION)).thenReturn(collectionReference);

        CollectionReference query = mock(CollectionReference.class);
        when(collectionReference.whereGreaterThanOrEqualTo(eq("orderDate"), anyString()))
                .thenReturn(query);
        when(query.whereLessThanOrEqualTo(eq("orderDate"), anyString()))
                .thenReturn(query);

        when(query.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(List.of());

        // Act
        RevenueReport report = firebaseRevenueService.getReport(startDate, endDate);

        // Assert
        assertEquals(0.0, report.getTotalRevenue());
        verify(firestore).collection(CollectionConstants.ORDER_COLLECTION);
        verify(querySnapshotFuture).get();
    }
}
