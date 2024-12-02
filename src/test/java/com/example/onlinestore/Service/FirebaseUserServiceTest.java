package com.example.onlinestore.Service;

import com.example.onlinestore.entity.User;
import com.example.onlinestore.service.FirebaseUserService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FirebaseUserServiceTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @Mock
    private ApiFuture<DocumentSnapshot> documentSnapshotFuture;

    @InjectMocks
    private FirebaseUserService firebaseUserService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Mock Firestore collection
        when(firestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(anyString())).thenReturn(documentReference);
    }

    @Test
    public void saveUser_ShouldReturnSuccessMessage() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            User user = new User();
            user.setUid("123");
            ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
            when(documentReference.set(user)).thenReturn(writeResultFuture);

            String result = firebaseUserService.saveUser(user);

            assertEquals("User added successfully with ID: 123", result);
            verify(documentReference).set(user);
        }
    }

    @Test
    public void deleteUserById_ShouldReturnSuccessMessage() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);

            String userId = "123";
            ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
            when(documentReference.delete()).thenReturn(writeResultFuture);

            String result = firebaseUserService.deleteUserById(userId);

            assertEquals("User deleted successfully", result);
            verify(documentReference).delete();
        }
    }

    @Test
    public void getUserById_ShouldReturnUserWhenExists() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            User user = new User();
            user.setUid("123");
            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
            when(documentSnapshot.exists()).thenReturn(true);
            when(documentSnapshot.toObject(User.class)).thenReturn(user);

            Optional<User> result = firebaseUserService.getUserById("123");

            assertTrue(result.isPresent());
            assertEquals(user, result.get());
        }
    }

    @Test
    public void getUserById_ShouldReturnEmptyWhenNotExists() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
            when(documentSnapshot.exists()).thenReturn(false);

            Optional<User> result = firebaseUserService.getUserById("123");

            assertTrue(result.isEmpty());
        }
    }

    @Test(expected = ExecutionException.class)
    public void getUserById_ShouldThrowExecutionException() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenThrow(new ExecutionException(new Throwable()));

            firebaseUserService.getUserById("123");
        }
    }

    @Test(expected = InterruptedException.class)
    public void getUserById_ShouldThrowInterruptedException() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> firestoreClientMockedStatic = mockStatic(FirestoreClient.class)) {
            firestoreClientMockedStatic.when(FirestoreClient::getFirestore).thenReturn(firestore);
            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenThrow(new InterruptedException());

            firebaseUserService.getUserById("123");
        }
    }

    @Test
    public void isCustomer_ShouldReturnTrueWhenUserHasCustomerRole() throws FirebaseAuthException {
        UserRecord userRecord = mock(UserRecord.class);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "CUSTOMER");
        when(userRecord.getCustomClaims()).thenReturn(claims);
        try (MockedStatic<FirebaseAuth> firebaseAuthMockedStatic = mockStatic(FirebaseAuth.class)) {
            FirebaseAuth firebaseAuth = mock(FirebaseAuth.class);
            firebaseAuthMockedStatic.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
            when(firebaseAuth.getUser("123")).thenReturn(userRecord);

            boolean result = firebaseUserService.isCustomer("123");

            assertTrue(result);
        }
    }

    @Test
    public void isCustomer_ShouldReturnFalseWhenUserDoesNotHaveCustomerRole() throws FirebaseAuthException {
        UserRecord userRecord = mock(UserRecord.class);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        when(userRecord.getCustomClaims()).thenReturn(claims);
        try (MockedStatic<FirebaseAuth> firebaseAuthMockedStatic = mockStatic(FirebaseAuth.class)) {
            FirebaseAuth firebaseAuth = mock(FirebaseAuth.class);
            firebaseAuthMockedStatic.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
            when(firebaseAuth.getUser("123")).thenReturn(userRecord);

            boolean result = firebaseUserService.isCustomer("123");

            assertFalse(result);
        }
    }

}