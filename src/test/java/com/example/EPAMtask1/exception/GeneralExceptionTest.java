package com.example.EPAMtask1.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeneralExceptionHandlerTest {

    private final GeneralExceptionHandler handler = new GeneralExceptionHandler();

    @Test
    void handleAuthentication_shouldReturn401() {
        ResponseEntity<ErrorResponse> response = handler.handlerAuthentication(
                new AuthenticationException("Invalid credentials"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody().getMessage());
    }

    @Test
    void handleIllegalArgument_shouldReturn404() {
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(
                new IllegalArgumentException("Not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", response.getBody().getMessage());
    }

    @Test
    void handleGeneralException_shouldReturn500WithSafeMessage() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneralException(
                new RuntimeException("Some internal detail that should not leak"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error occurred", response.getBody().getMessage());
    }
}