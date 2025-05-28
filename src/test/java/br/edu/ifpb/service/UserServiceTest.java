package br.edu.ifpb.service;

import br.edu.ifpb.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final String COLLECTION_NAME = "users";

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionRef;

    @Mock
    private DocumentReference docRef;

    @Mock
    private ApiFuture<DocumentSnapshot> futureDocSnapshot;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @Mock
    private ApiFuture<WriteResult> futureWriteResult;

    @Mock
    private WriteResult writeResult;

    @Mock
    private ApiFuture<QuerySnapshot> futureQuerySnapshot;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private QueryDocumentSnapshot queryDocSnap;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        when(firestore.collection(COLLECTION_NAME)).thenReturn(collectionRef);
    }

    @Test
    void testRegisterValidUser() throws ExecutionException, InterruptedException {
        User user = new User("John Doe", 30, 5000.0, "john@example.com", "123456789", "Software Engineer");

        when(collectionRef.document(user.getEmail())).thenReturn(docRef);
        when(docRef.get()).thenReturn(futureDocSnapshot);
        when(futureDocSnapshot.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);
        when(docRef.set(user)).thenReturn(futureWriteResult);
        when(futureWriteResult.get()).thenReturn(writeResult);

        User saved = userService.registerUser(user);

        assertNotNull(saved);
        assertEquals(user.getEmail(), saved.getEmail());
        verify(docRef, times(1)).set(user);
    }

    @Test
    void testRegisterUserWithInvalidEmail() {
        User user = new User("John Doe", 30, 5000.0, "invalid-email", "123456789", "Software Engineer");

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verify(firestore, never()).collection(anyString());
    }

    @Test
    void testRegisterUserWithNegativeAge() {
        User user = new User("John Doe", -1, 5000.0, "john@example.com", "123456789", "Software Engineer");

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verify(firestore, never()).collection(anyString());
    }

    @Test
    void testRegisterUserWithNegativeSalary() {
        User user = new User("John Doe", 30, -5000.0, "john@example.com", "123456789", "Software Engineer");

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verify(firestore, never()).collection(anyString());
    }

    @Test
    void testFindUserByEmail() throws ExecutionException, InterruptedException {
        String email = "john@example.com";
        User user = new User("John Doe", 30, 5000.0, email, "123456789", "Software Engineer");

        when(collectionRef.document(email)).thenReturn(docRef);
        when(docRef.get()).thenReturn(futureDocSnapshot);
        when(futureDocSnapshot.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(User.class)).thenReturn(user);

        Optional<User> result = userService.findUserByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }

    @Test
    void testGetAllUsers() throws ExecutionException, InterruptedException {
        User u1 = new User("John Doe", 30, 5000.0, "john@example.com", "123456789", "Software Engineer");
        User u2 = new User("Jane Doe", 28, 6000.0, "jane@example.com", "987654321", "Product Manager");

        when(collectionRef.get()).thenReturn(futureQuerySnapshot);
        when(futureQuerySnapshot.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(queryDocSnap, queryDocSnap));
        when(queryDocSnap.toObject(User.class)).thenReturn(u1, u2);

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(collectionRef, times(1)).get();
    }

    @Test
    void testDeleteUser() throws ExecutionException, InterruptedException {
        String email = "john@example.com";

        when(collectionRef.document(email)).thenReturn(docRef);
        when(docRef.delete()).thenReturn(futureWriteResult);
        when(futureWriteResult.get()).thenReturn(writeResult);

        userService.deleteUser(email);

        verify(docRef, times(1)).delete();
    }
}
