package com.example.EPAMtask1.services;

import com.example.EPAMtask1.model.User;
import com.example.EPAMtask1.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void authenticate_shouldPass_whenCredentialsValid() {
        User user = new User();
        user.setUsername("john.doe");
        user.setPassword("password123");
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> authenticationService.authenticate("john.doe", "password123"));
    }

    @Test
    void authenticate_shouldThrow_whenUsernameNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> authenticationService.authenticate("unknown", "password123"));
    }

    @Test
    void authenticate_shouldThrow_whenPasswordIncorrect() {
        User user = new User();
        user.setUsername("john.doe");
        user.setPassword("correctPassword");
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> authenticationService.authenticate("john.doe", "wrongPassword"));
    }
}