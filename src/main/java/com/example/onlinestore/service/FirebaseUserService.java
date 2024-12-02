package com.example.onlinestore.service;

import com.example.onlinestore.constants.CollectionConstants;
import com.example.onlinestore.constants.RoleConstants;
import com.example.onlinestore.entity.User;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseUserService {
    // Role Management Section

    private static final String COLLECTION_NAME = CollectionConstants.USERS_COLLECTION;

    /**
     * Assigns the 'ADMIN' role to a Firebase user by setting a custom claim.
     *
     * @param uid The UID of the Firebase user.
     * @throws FirebaseAuthException If there is an error setting the custom claim.
     */
    public void setAdminRole(String uid) throws FirebaseAuthException {
        Map<String, Object> claims = new HashMap<>();
        claims.put(RoleConstants.KEY, RoleConstants.ROLE_ADMIN);
        FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);
        System.out.println("Admin role assigned to user with UID: " + uid);
    }

    /**
     * Assigns the 'CUSTOMER' role to a Firebase user by setting a custom claim.
     *
     * @param uid The UID of the Firebase user.
     * @throws FirebaseAuthException If there is an error setting the custom claim.
     */
    public void setCustomerRole(String uid) throws FirebaseAuthException {
        Map<String, Object> claims = new HashMap<>();
        claims.put(RoleConstants.KEY, RoleConstants.ROLE_CUSTOMER);
        FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);
        System.out.println("Customer role assigned to user with UID: " + uid);
    }

    /**
     * Removes all roles from a Firebase user by clearing the custom claims.
     *
     * @param uid The UID of the Firebase user.
     * @throws FirebaseAuthException If there is an error clearing the custom claims.
     */
    public void removeUserRole(String uid) throws FirebaseAuthException {
        FirebaseAuth.getInstance().setCustomUserClaims(uid, new HashMap<>());
        System.out.println("Roles removed for user with UID: " + uid);
    }

    /**
     * Checks if a user has the 'ADMIN' role.
     *
     * @param uid The UID of the Firebase user.
     * @return true if the user has the 'ADMIN' role, false otherwise.
     * @throws FirebaseAuthException If there is an error while retrieving user claims.
     */
    public boolean isAdmin(String uid) throws FirebaseAuthException {
        UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);
        Map<String, Object> claims = userRecord.getCustomClaims();
        return "ADMIN".equals(claims.get("role"));
    }

    /**
     * Checks if a user has the 'CUSTOMER' role.
     *
     * @param uid The UID of the Firebase user.
     * @return true if the user has the 'CUSTOMER' role, false otherwise.
     * @throws FirebaseAuthException If there is an error while retrieving user claims.
     */
    public boolean isCustomer(String uid) throws FirebaseAuthException {
        UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);
        Map<String, Object> claims = userRecord.getCustomClaims();
        return "CUSTOMER".equals(claims.get("role"));
    }

    // Firestore User Data Management Section

    /**
     * Saves user details to Firestore. Generates a new ID if none is provided.
     *
     * @param user The User object to be saved.
     * @return A success message with the user's ID.
     * @throws ExecutionException, InterruptedException if there are Firestore access issues.
     */
    public String saveUser(User user) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        // Generate a new ID if the user ID is null or empty
        if (user.getUid() == null || user.getUid().trim().isEmpty()) {
            user.setUid(dbFirestore.collection(COLLECTION_NAME).document().getId());
        }

        dbFirestore.collection(COLLECTION_NAME).document(user.getUid()).set(user).get();
        return user.getUid();
    }

    /**
     * Retrieves user details by ID from Firestore.
     *
     * @param id The ID of the user to retrieve.
     * @return An Optional containing the User object if found, otherwise empty.
     * @throws ExecutionException, InterruptedException if there are Firestore access issues.
     */
    public Optional<User> getUserById(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentSnapshot document = dbFirestore.collection(COLLECTION_NAME).document(id).get().get();

        if (document.exists()) {
            return Optional.ofNullable(document.toObject(User.class));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Deletes user details by ID from Firestore.
     *
     * @param id The ID of the user to delete.
     * @return A success message confirming deletion.
     * @throws ExecutionException, InterruptedException if there are Firestore access issues.
     */
    public String deleteUserById(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        dbFirestore.collection(COLLECTION_NAME).document(id).delete().get();
        return "User deleted successfully";
    }
}
