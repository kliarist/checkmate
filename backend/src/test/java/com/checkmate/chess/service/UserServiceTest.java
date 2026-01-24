package com.checkmate.chess.service;

import com.checkmate.chess.model.User;
import com.checkmate.chess.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPasswordHash("hashedPassword");
    }

    @Test
    void registerUser_WithValidData_ShouldSucceed() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.registerUser("test@example.com", "testuser", "password123");

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_WithDuplicateEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser("test@example.com", "newuser", "password123");
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithDuplicateUsername_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser("new@example.com", "testuser", "password123");
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithInvalidEmail_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser("invalid-email", "testuser", "password123");
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithShortPassword_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser("test@example.com", "testuser", "pass");
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithPasswordWithoutNumber_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser("test@example.com", "testuser", "password");
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithNullEmail_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(null, "testuser", "password123");
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithEmptyUsername_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser("test@example.com", "", "password123");
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldHashPassword() {
        // Arrange
        String plainPassword = "password123";
        String hashedPassword = "hashedPassword";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(plainPassword)).thenReturn(hashedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.registerUser("test@example.com", "testuser", plainPassword);

        // Assert
        verify(passwordEncoder).encode(plainPassword);
        verify(userRepository).save(argThat(user -> 
            user.getPasswordHash().equals(hashedPassword)
        ));
    }

    @Test
    void registerUser_ShouldNotStoreP

lainPassword() {
        // Arrange
        String plainPassword = "password123";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.registerUser("test@example.com", "testuser", plainPassword);

        // Assert
        verify(userRepository).save(argThat(user -> 
            !user.getPasswordHash().equals(plainPassword)
        ));
    }
}
