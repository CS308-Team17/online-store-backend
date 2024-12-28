package com.example.onlinestore.Service;

import com.example.onlinestore.entity.Category;
import com.example.onlinestore.entity.OrderDetails;
import com.example.onlinestore.service.FirebaseCategoryService;
import com.example.onlinestore.service.FirebaseOrderService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FirebaseCategoryServiceTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private Query query;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotFuture;

    @InjectMocks
    private FirebaseCategoryService firebaseCategoryService;

    @InjectMocks
    private FirebaseOrderService firebaseOrderService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Mock Firestore collection
        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(anyString())).thenReturn(documentReference);
    }

    @Test
    public void deleteCategoryById_ShouldReturnSuccessMessage() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            String categoryId = "123";
            ApiFuture<WriteResult> mockApiFuture = mock(ApiFuture.class);
            WriteResult mockWriteResult = mock(WriteResult.class);
            when(mockApiFuture.get()).thenReturn(mockWriteResult);

            when(collectionReference.document(categoryId)).thenReturn(documentReference);
            when(documentReference.delete()).thenReturn(mockApiFuture);

            String result = firebaseCategoryService.deleteCategoryById(categoryId);

            assertEquals("Category deleted successfully", result);
            verify(collectionReference).document(categoryId);
            verify(documentReference).delete();
        }
    }

    @Test
    public void getCategoryById_ShouldReturnCategory() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            String categoryId = "123";
            Category category = new Category();
            category.setId(categoryId);
            category.setName("Electronics");

            ApiFuture<DocumentSnapshot> mockApiFuture = mock(ApiFuture.class);
            DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
            when(documentReference.get()).thenReturn(mockApiFuture);
            when(mockApiFuture.get()).thenReturn(mockDocumentSnapshot);
            when(mockDocumentSnapshot.toObject(Category.class)).thenReturn(category);

            Category result = firebaseCategoryService.getCategoryById(categoryId);

            assertEquals(categoryId, result.getId());
            assertEquals("Electronics", result.getName());
            verify(collectionReference).document(categoryId);
        }
    }

    @Test
    public void getAll_ShouldReturnEmptyListWhenNoCategories() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            when(collectionReference.get()).thenReturn(querySnapshotFuture);
            when(querySnapshotFuture.get()).thenReturn(querySnapshot);
            when(querySnapshot.toObjects(Category.class)).thenReturn(Arrays.asList());

            List<Category> result = firebaseCategoryService.getAll();

            assertEquals(0, result.size());
        }
    }


    @Test
    public void getOrdersByUserId_ShouldReturnEmptyListWhenNoOrders() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            ApiFuture<QuerySnapshot> mockQuerySnapshotFuture = mock(ApiFuture.class);
            QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);

            when(firestore.collection("orders")).thenReturn(collectionReference);
            when(collectionReference.whereEqualTo("uid", "user123")).thenReturn(query);
            when(query.get()).thenReturn(mockQuerySnapshotFuture);
            when(mockQuerySnapshotFuture.get()).thenReturn(mockQuerySnapshot);
            when(mockQuerySnapshot.toObjects(OrderDetails.class)).thenReturn(Arrays.asList());

            List<OrderDetails> result = firebaseOrderService.getOrdersByUserId("user123");

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test(expected = RuntimeException.class)
    public void getOrdersByUserId_ShouldThrowRuntimeExceptionOnFirestoreFailure() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            ApiFuture<QuerySnapshot> mockQuerySnapshotFuture = mock(ApiFuture.class);

            when(firestore.collection("orders")).thenReturn(collectionReference);
            when(collectionReference.whereEqualTo("uid", "user123")).thenReturn(query);
            when(query.get()).thenReturn(mockQuerySnapshotFuture);
            when(mockQuerySnapshotFuture.get()).thenThrow(new ExecutionException(new Throwable()));

            firebaseOrderService.getOrdersByUserId("user123");
        }
    }
}
