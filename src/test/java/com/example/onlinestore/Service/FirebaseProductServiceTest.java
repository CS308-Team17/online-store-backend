package com.example.onlinestore.Service;

import com.example.onlinestore.entity.Category;
import com.example.onlinestore.entity.Product;
import com.example.onlinestore.payload.ProductPayload;
import com.example.onlinestore.service.FirebaseProductService;
import com.example.onlinestore.service.FirebaseReviewService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FirebaseProductServiceTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotFuture;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private ApiFuture<WriteResult> writeResultFuture;

    @Mock
    private WriteResult writeResult;

    @Mock
    private FirebaseReviewService firebaseReviewService;

    @InjectMocks
    private FirebaseProductService firebaseProductService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(anyString())).thenReturn(documentReference);
    }

    @Test
    public void getAllProducts_ShouldReturnListOfProducts() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
            when(firestore.collection("products").get()).thenReturn(future);
            QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
            when(future.get()).thenReturn(querySnapshot);
            List<QueryDocumentSnapshot> documents = Arrays.asList(mock(QueryDocumentSnapshot.class));
            when(querySnapshot.getDocuments()).thenReturn(documents);
            Product product = new Product();
            when(documents.get(0).toObject(Product.class)).thenReturn(product);

            List<Product> result = firebaseProductService.getAllProducts();

            assertEquals(1, result.size());
            assertEquals(product, result.get(0));
        }
    }

    @Test
    public void getAllProducts_ShouldReturnEmptyListWhenNoProducts() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
            when(firestore.collection("products").get()).thenReturn(future);
            QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
            when(future.get()).thenReturn(querySnapshot);
            when(querySnapshot.getDocuments()).thenReturn(Arrays.asList());

            List<Product> result = firebaseProductService.getAllProducts();

            assertTrue(result.isEmpty());
        }
    }

    @Test(expected = ExecutionException.class)
    public void getAllProducts_ShouldThrowExecutionException() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
            when(firestore.collection("products").get()).thenReturn(future);
            when(future.get()).thenThrow(new ExecutionException(new Throwable()));

            firebaseProductService.getAllProducts();
        }
    }
    @Test
    public void decrementWishlistCount_ShouldDecrementWishlistCountSuccessfully() {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            DocumentReference documentReference = mock(DocumentReference.class);
            when(firestore.collection("products").document("product1")).thenReturn(documentReference);
            ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
            when(documentReference.update(eq("numOfWishlists"), any())).thenReturn(writeResultFuture);

            firebaseProductService.decrementWishlistCount("product1");

            verify(documentReference).update("numOfWishlists", com.google.cloud.firestore.FieldValue.increment(-1));
        }
    }

    @Test(expected = RuntimeException.class)
    public void getMostWishlistedProducts_ShouldThrowRuntimeExceptionWhenErrorOccurs() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
            when(firestore.collection("products").orderBy("numOfWishlists", com.google.cloud.firestore.Query.Direction.DESCENDING).limit(8).get()).thenReturn(future);
            when(future.get()).thenThrow(new InterruptedException());

            firebaseProductService.getMostWishlistedProducts();
        }
    }

    @Test(expected = RuntimeException.class)
    public void decrementWishlistCount_ShouldThrowRuntimeExceptionWhenFirestoreFails() {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            DocumentReference documentReference = mock(DocumentReference.class);
            when(firestore.collection("products").document("product1")).thenReturn(documentReference);
            when(documentReference.update(eq("numOfWishlists"), any())).thenThrow(new RuntimeException("Firestore error"));

            firebaseProductService.decrementWishlistCount("product1");
        }
    }

    @Test
    public void saveProduct_ShouldReturnSuccessMessage() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            ProductPayload productPayload = new ProductPayload();
            DocumentReference documentReference = mock(DocumentReference.class);
            when(firestore.collection("products").document()).thenReturn(documentReference);
            when(documentReference.getId()).thenReturn("123");
            ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
            when(documentReference.set(any(Product.class))).thenReturn(writeResultFuture);

            String result = firebaseProductService.saveProduct(productPayload);

            assertEquals("Product added successfully with ID: 123", result);
        }
    }

    @Test
    public void deleteProductById_ShouldReturnSuccessMessage() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
            when(documentReference.delete()).thenReturn(writeResultFuture);

            String result = firebaseProductService.deleteProductById("123");

            assertEquals("Product deleted successfully", result);
        }
    }

    @Test
    public void getProductById_ShouldReturnProduct() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            Product product = new Product();
            ApiFuture<DocumentSnapshot> documentSnapshotFuture = mock(ApiFuture.class);
            DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
            when(documentSnapshot.toObject(Product.class)).thenReturn(product);

            Product result = firebaseProductService.getProductById("123");

            assertEquals(product, result);
        }
    }

    @Test
    public void decreaseQuantityInStock_ShouldReturnSuccessMessage() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            Product product = new Product();
            product.setQuantityInStock(10);
            ApiFuture<DocumentSnapshot> documentSnapshotFuture = mock(ApiFuture.class);
            DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
            when(documentSnapshot.toObject(Product.class)).thenReturn(product);
            ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
            when(documentReference.update(eq("quantityInStock"), anyInt())).thenReturn(writeResultFuture);

            String result = firebaseProductService.decreaseQuantityInStock("123", 5);

            assertEquals("Quantity in stock decreased successfully", result);
        }
    }

    @Test
    public void decreaseQuantityInStock_ShouldReturnNotEnoughQuantity() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            Product product = new Product();
            product.setQuantityInStock(3);
            ApiFuture<DocumentSnapshot> documentSnapshotFuture = mock(ApiFuture.class);
            DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
            when(documentSnapshot.toObject(Product.class)).thenReturn(product);

            String result = firebaseProductService.decreaseQuantityInStock("123", 5);

            assertEquals("Not enough quantity in stock", result);
        }
    }



}