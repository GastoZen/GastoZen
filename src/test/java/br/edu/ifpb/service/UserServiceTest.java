package br.edu.ifpb.service;

import br.edu.ifpb.model.User;
import br.edu.ifpb.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void testRegisterValidUser() {
        User user = new User("John Doe", 30, 5000.0, "john@example.com", "123456789", "Software Engineer");
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.registerUser(user);

        assertNotNull(savedUser);
        assertEquals("John Doe", savedUser.getName());
        verify(userRepository).save(user);
    }

    @Test
    void testRegisterUserWithInvalidEmail() {
        User user = new User("John Doe", 30, 5000.0, "invalid-email", "123456789", "Software Engineer");

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterUserWithNegativeAge() {
        User user = new User("John Doe", -1, 5000.0, "john@example.com", "123456789", "Software Engineer");

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterUserWithNegativeSalary() {
        User user = new User("John Doe", 30, -5000.0, "john@example.com", "123456789", "Software Engineer");

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testFindUserByEmail() {
        String email = "john@example.com";
        User user = new User("John Doe", 30, 5000.0, email, "123456789", "Software Engineer");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findUserByEmail(email);

        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(
            new User("John Doe", 30, 5000.0, "john@example.com", "123456789", "Software Engineer"),
            new User("Jane Doe", 28, 6000.0, "jane@example.com", "987654321", "Product Manager")
        );
        when(userRepository.findAll()).thenReturn(users);

        List<User> allUsers = userService.getAllUsers();

        assertEquals(2, allUsers.size());
        verify(userRepository).findAll();
    }

    @Test
    void testDeleteUser() {
        String email = "john@example.com";
        userService.deleteUser(email);
        verify(userRepository).delete(email);
    }
}