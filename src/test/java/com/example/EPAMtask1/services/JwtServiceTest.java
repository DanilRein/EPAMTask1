package com.example.EPAMtask1.services;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void generateToken_shouldReturnNonEmptyToken() {
        String token = jwtService.generateToken("john.doe");

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void extractUsername_shouldReturnOriginalUsername() {
        String token = jwtService.generateToken("john.doe");

        String username = jwtService.extractUsername(token);

        assertEquals("john.doe", username);
    }

    @Test
    void extractUsername_shouldThrow_forMalformedToken() {
        assertThrows(JwtException.class, () -> jwtService.extractUsername("not-a-valid-token"));
    }

    @Test
    void extractUsername_shouldThrow_forTokenSignedWithDifferentKey() {
        JwtService otherJwtService = new JwtService();
        String token = otherJwtService.generateToken("john.doe");

        assertThrows(JwtException.class, () -> jwtService.extractUsername(token));
    }
}
