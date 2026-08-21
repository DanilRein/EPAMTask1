package com.example.EPAMtask1.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest {

    private final LoginAttemptService service = new LoginAttemptService();

    @Test
    void isBlocked_shouldReturnFalse_forUnknownUser() {
        assertFalse(service.isBlocked("unknown.user"));
    }

    @Test
    void isBlocked_shouldReturnFalse_afterOneOrTwoFailures() {
        service.loginFailed("john.doe");
        assertFalse(service.isBlocked("john.doe"));

        service.loginFailed("john.doe");
        assertFalse(service.isBlocked("john.doe"));
    }

    @Test
    void isBlocked_shouldReturnTrue_afterThreeFailures() {
        service.loginFailed("john.doe");
        service.loginFailed("john.doe");
        service.loginFailed("john.doe");

        assertTrue(service.isBlocked("john.doe"));
    }

    @Test
    void isBlocked_shouldNotBlock_afterMoreFailuresIfAlreadyUnblockedExpired() {
        service.loginFailed("john.doe");
        service.loginFailed("john.doe");
        service.loginFailed("john.doe");
        // simulate the block window having already expired
        service.blockedUntil.put("john.doe", LocalDateTime.now().minusSeconds(1));

        assertFalse(service.isBlocked("john.doe"));
        // expiry clears both maps, so a new failure should start counting from scratch
        assertFalse(service.attempts.containsKey("john.doe"));
    }

    @Test
    void loginSucceed_shouldClearAttemptsAndBlock() {
        service.loginFailed("john.doe");
        service.loginFailed("john.doe");
        service.loginFailed("john.doe");
        assertTrue(service.isBlocked("john.doe"));

        service.loginSucceed("john.doe");

        assertFalse(service.isBlocked("john.doe"));
        assertFalse(service.attempts.containsKey("john.doe"));
    }
}
