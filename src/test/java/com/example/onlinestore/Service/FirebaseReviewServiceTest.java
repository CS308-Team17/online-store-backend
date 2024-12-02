package com.example.onlinestore.Service;

import com.example.onlinestore.entity.Review;
import com.example.onlinestore.enums.ReviewStatus;
import com.example.onlinestore.payload.ReviewPayload;
import com.example.onlinestore.service.FirebaseReviewService;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
}