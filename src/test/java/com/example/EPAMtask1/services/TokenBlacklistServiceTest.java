package com.example.EPAMtask1.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenBlacklistServiceTest {

    private final TokenBlacklistService service = new TokenBlacklistService();

    @Test
    void isBlackListed_shouldReturnFalse_forUnknownToken() {
        assertFalse(service.isBlackListed("some-token"));
    }

    @Test
    void blacklist_shouldMarkTokenAsBlackListed() {
        service.blacklist("jwt-token-123");

        assertTrue(service.isBlackListed("jwt-token-123"));
    }

    @Test
    void isBlackListed_shouldReturnFalse_forDifferentToken() {
        service.blacklist("jwt-token-123");

        assertFalse(service.isBlackListed("another-token"));
    }
}
