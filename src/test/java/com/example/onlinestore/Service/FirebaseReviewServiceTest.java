package com.example.onlinestore.Service;

import com.example.onlinestore.entity.Product;
import com.example.onlinestore.entity.Review;
import com.example.onlinestore.entity.User;
import com.example.onlinestore.enums.ReviewStatus;
import com.example.onlinestore.payload.ReviewPayload;
import com.example.onlinestore.service.FirebaseProductService;
import com.example.onlinestore.service.FirebaseReviewService;
import com.example.onlinestore.service.FirebaseUserService;
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
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FirebaseReviewServiceTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private FirebaseUserService firebaseUserService;

    @Mock
    private FirebaseProductService firebaseProductService;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotFuture;

    @Mock
    private ApiFuture<WriteResult> writeResultFuture;

    @InjectMocks
    private FirebaseReviewService firebaseReviewService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(anyString())).thenReturn(documentReference);
    }

    @Test
    public void deleteReviewById_ShouldReturnSuccessMessage() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
            when(documentReference.delete()).thenReturn(writeResultFuture);

            String result = firebaseReviewService.deleteReview("123");

            assertEquals("Review deleted successfully", result);
        }
    }

    @Test
    public void getReviewById_ShouldReturnReview() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            Review review = new Review();
            ApiFuture<DocumentSnapshot> documentSnapshotFuture = mock(ApiFuture.class);
            DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
            when(documentSnapshot.toObject(Review.class)).thenReturn(review);

            Review result = firebaseReviewService.getReviewById("123");

            assertEquals(review, result);
        }
    }

    @Test
    public void getAllReviews_ShouldReturnEmptyListWhenNoReviews() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
            when(firestore.collection("reviews").get()).thenReturn(future);
            QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
            when(future.get()).thenReturn(querySnapshot);
            when(querySnapshot.getDocuments()).thenReturn(Arrays.asList());

            List<Review> result = firebaseReviewService.getAllReviews();

            assertTrue(result.isEmpty());
        }
    }

    @Test(expected = ExecutionException.class)
    public void getAllReviews_ShouldThrowExecutionException() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
            when(firestore.collection("reviews").get()).thenReturn(future);
            when(future.get()).thenThrow(new ExecutionException(new Throwable()));

            firebaseReviewService.getAllReviews();
        }
    }

    @Test
    public void setReviewStatus_ShouldReturnSuccessMessage() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            // Mock Firestore operations
            ApiFuture<WriteResult> mockWriteResult = mock(ApiFuture.class);
            WriteResult mockWriteResultValue = mock(WriteResult.class);
            when(collectionReference.document("123")).thenReturn(documentReference);
            when(documentReference.update("reviewStatus", ReviewStatus.APPROVED)).thenReturn(mockWriteResult);
            when(mockWriteResult.get()).thenReturn(mockWriteResultValue); // Mock successful update

            // Call the method
            String result = firebaseReviewService.setReviewStatus("123", ReviewStatus.APPROVED);

            // Validate result
            assertEquals("Review status updated successfully", result);

            // Verify interactions
            verify(documentReference).update("reviewStatus", ReviewStatus.APPROVED);
        }
    }
    @Test
    public void getReviewsByProductId_ShouldReturnReviews() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            // Mock Firestore operations
            ApiFuture<QuerySnapshot> mockQuerySnapshotFuture = mock(ApiFuture.class);
            QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
            Review review1 = new Review();
            review1.setReviewId("review1");
            review1.setProductId("product123");

            Review review2 = new Review();
            review2.setReviewId("review2");
            review2.setProductId("product123");

            List<Review> mockReviews = Arrays.asList(review1, review2);

            when(firestore.collection(anyString())).thenReturn(collectionReference);
            when(collectionReference.whereEqualTo("productId", "product123")).thenReturn(collectionReference);
            when(collectionReference.get()).thenReturn(mockQuerySnapshotFuture);
            when(mockQuerySnapshotFuture.get()).thenReturn(mockQuerySnapshot);
            when(mockQuerySnapshot.toObjects(Review.class)).thenReturn(mockReviews);

            // Call the method
            List<Review> result = firebaseReviewService.getReviewsByProductId("product123");

            // Validate result
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("review1", result.get(0).getReviewId());
            assertEquals("review2", result.get(1).getReviewId());
        }
    }

    @Test
    public void getReviewsByProductId_ShouldReturnEmptyListWhenNoReviews() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            // Mock Firestore operations
            ApiFuture<QuerySnapshot> mockQuerySnapshotFuture = mock(ApiFuture.class);
            QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);

            when(firestore.collection(anyString())).thenReturn(collectionReference);
            when(collectionReference.whereEqualTo("productId", "product123")).thenReturn(collectionReference);
            when(collectionReference.get()).thenReturn(mockQuerySnapshotFuture);
            when(mockQuerySnapshotFuture.get()).thenReturn(mockQuerySnapshot);
            when(mockQuerySnapshot.toObjects(Review.class)).thenReturn(Arrays.asList()); // Return empty list

            // Call the method
            List<Review> result = firebaseReviewService.getReviewsByProductId("product123");

            // Validate result
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test(expected = ExecutionException.class)
    public void getReviewsByProductId_ShouldThrowExecutionException() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            // Mock Firestore operations
            ApiFuture<QuerySnapshot> mockQuerySnapshotFuture = mock(ApiFuture.class);

            when(firestore.collection(anyString())).thenReturn(collectionReference);
            when(collectionReference.whereEqualTo("productId", "product123")).thenReturn(collectionReference);
            when(collectionReference.get()).thenReturn(mockQuerySnapshotFuture);
            when(mockQuerySnapshotFuture.get()).thenThrow(new ExecutionException(new Throwable())); // Simulate exception

            // Call the method
            firebaseReviewService.getReviewsByProductId("product123");
        }
    }

    @Test
    public void getReviewsByUserId_ShouldReturnReviews() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            // Mock Firestore operations
            ApiFuture<QuerySnapshot> mockQuerySnapshotFuture = mock(ApiFuture.class);
            QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
            Review review1 = new Review();
            review1.setReviewId("review1");
            review1.setUid("user123");

            Review review2 = new Review();
            review2.setReviewId("review2");
            review2.setUid("user123");

            List<Review> mockReviews = Arrays.asList(review1, review2);

            when(firestore.collection(anyString())).thenReturn(collectionReference);
            when(collectionReference.whereEqualTo("uid", "user123")).thenReturn(collectionReference);
            when(collectionReference.get()).thenReturn(mockQuerySnapshotFuture);
            when(mockQuerySnapshotFuture.get()).thenReturn(mockQuerySnapshot);
            when(mockQuerySnapshot.toObjects(Review.class)).thenReturn(mockReviews);

            // Call the method
            List<Review> result = firebaseReviewService.getReviewsByUserId("user123");

            // Validate result
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("review1", result.get(0).getReviewId());
            assertEquals("review2", result.get(1).getReviewId());
        }
    }

    @Test
    public void getReviewsByUserId_ShouldReturnEmptyListWhenNoReviews() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            // Mock Firestore operations
            ApiFuture<QuerySnapshot> mockQuerySnapshotFuture = mock(ApiFuture.class);
            QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);

            when(firestore.collection(anyString())).thenReturn(collectionReference);
            when(collectionReference.whereEqualTo("uid", "user123")).thenReturn(collectionReference);
            when(collectionReference.get()).thenReturn(mockQuerySnapshotFuture);
            when(mockQuerySnapshotFuture.get()).thenReturn(mockQuerySnapshot);
            when(mockQuerySnapshot.toObjects(Review.class)).thenReturn(Arrays.asList()); // Return empty list

            // Call the method
            List<Review> result = firebaseReviewService.getReviewsByUserId("user123");

            // Validate result
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test(expected = ExecutionException.class)
    public void getReviewsByUserId_ShouldThrowExecutionException() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            // Mock Firestore operations
            ApiFuture<QuerySnapshot> mockQuerySnapshotFuture = mock(ApiFuture.class);

            when(firestore.collection(anyString())).thenReturn(collectionReference);
            when(collectionReference.whereEqualTo("uid", "user123")).thenReturn(collectionReference);
            when(collectionReference.get()).thenReturn(mockQuerySnapshotFuture);
            when(mockQuerySnapshotFuture.get()).thenThrow(new ExecutionException(new Throwable())); // Simulate exception

            // Call the method
            firebaseReviewService.getReviewsByUserId("user123");
        }
    }
    @Test
    public void getReviewsByStatus_ShouldReturnReviews() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            // Mock Firestore operations
            ApiFuture<QuerySnapshot> mockQuerySnapshotFuture = mock(ApiFuture.class);
            QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);

            Review review1 = new Review();
            review1.setReviewId("review1");
            review1.setReviewStatus(ReviewStatus.APPROVED);

            Review review2 = new Review();
            review2.setReviewId("review2");
            review2.setReviewStatus(ReviewStatus.APPROVED);

            List<Review> mockReviews = Arrays.asList(review1, review2);

            when(firestore.collection(anyString())).thenReturn(collectionReference);
            when(collectionReference.whereEqualTo("reviewStatus", ReviewStatus.APPROVED)).thenReturn(collectionReference);
            when(collectionReference.get()).thenReturn(mockQuerySnapshotFuture);
            when(mockQuerySnapshotFuture.get()).thenReturn(mockQuerySnapshot);
            when(mockQuerySnapshot.toObjects(Review.class)).thenReturn(mockReviews);

            // Call the method
            List<Review> result = firebaseReviewService.getReviewsByStatus(ReviewStatus.APPROVED);

            // Validate result
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("review1", result.get(0).getReviewId());
            assertEquals("review2", result.get(1).getReviewId());
        }
    }

    @Test
    public void getReviewsByStatus_ShouldReturnEmptyListWhenNoReviews() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            // Mock Firestore operations
            ApiFuture<QuerySnapshot> mockQuerySnapshotFuture = mock(ApiFuture.class);
            QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);

            when(firestore.collection(anyString())).thenReturn(collectionReference);
            when(collectionReference.whereEqualTo("reviewStatus", ReviewStatus.PENDING)).thenReturn(collectionReference);
            when(collectionReference.get()).thenReturn(mockQuerySnapshotFuture);
            when(mockQuerySnapshotFuture.get()).thenReturn(mockQuerySnapshot);
            when(mockQuerySnapshot.toObjects(Review.class)).thenReturn(Arrays.asList()); // Return empty list

            // Call the method
            List<Review> result = firebaseReviewService.getReviewsByStatus(ReviewStatus.PENDING);

            // Validate result
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test(expected = ExecutionException.class)
    public void getReviewsByStatus_ShouldThrowExecutionException() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            // Mock Firestore operations
            ApiFuture<QuerySnapshot> mockQuerySnapshotFuture = mock(ApiFuture.class);

            when(firestore.collection(anyString())).thenReturn(collectionReference);
            when(collectionReference.whereEqualTo("reviewStatus", ReviewStatus.DECLINED)).thenReturn(collectionReference);
            when(collectionReference.get()).thenReturn(mockQuerySnapshotFuture);
            when(mockQuerySnapshotFuture.get()).thenThrow(new ExecutionException(new Throwable())); // Simulate exception

            // Call the method
            firebaseReviewService.getReviewsByStatus(ReviewStatus.DECLINED);
        }
    }

    @Test
    public void makeProductNullInReviews_ShouldDoNothingWhenNoReviews() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            // Mock Firestore operations
            ApiFuture<QuerySnapshot> mockQuerySnapshotFuture = mock(ApiFuture.class);
            QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);

            when(firestore.collection(anyString())).thenReturn(collectionReference);
            when(collectionReference.whereEqualTo("productId", "product123")).thenReturn(collectionReference);
            when(collectionReference.get()).thenReturn(mockQuerySnapshotFuture);
            when(mockQuerySnapshotFuture.get()).thenReturn(mockQuerySnapshot);
            when(mockQuerySnapshot.toObjects(Review.class)).thenReturn(Arrays.asList()); // No reviews

            // Call the method
            firebaseReviewService.makeProductNullInReviews("product123");

            // Validate that no updates were made
            verify(collectionReference, never()).document(anyString());
        }
    }

    @Test(expected = ExecutionException.class)
    public void makeProductNullInReviews_ShouldThrowExecutionException() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            // Mock Firestore operations
            ApiFuture<QuerySnapshot> mockQuerySnapshotFuture = mock(ApiFuture.class);

            when(firestore.collection(anyString())).thenReturn(collectionReference);
            when(collectionReference.whereEqualTo("productId", "product123")).thenReturn(collectionReference);
            when(collectionReference.get()).thenReturn(mockQuerySnapshotFuture);
            when(mockQuerySnapshotFuture.get()).thenThrow(new ExecutionException(new Throwable())); // Simulate exception

            // Call the method
            firebaseReviewService.makeProductNullInReviews("product123");
        }
    }



}