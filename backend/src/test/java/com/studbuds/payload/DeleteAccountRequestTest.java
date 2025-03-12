package com.studbuds.payload;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeleteAccountRequestTest {
    private DeleteAccountRequest request;

    @BeforeEach
    void setUp() {
        request = new DeleteAccountRequest();
    }

    @Test
    void testValidEmail() {
        String validEmail = "user@cooper.edu";
        request.setEmail(validEmail);
        assertEquals(validEmail, request.getEmail());
    }

    @Test
    void testInvalidEmail() {
        String invalidEmail = "user@gmail.com";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> request.setEmail(invalidEmail));
        assertEquals("Email must be a @cooper.edu address", exception.getMessage());
    }

    @Test
    void testValidPassword() {
        String validPassword = "securePass1";
        request.setPassword(validPassword);
        assertEquals(validPassword, request.getPassword());
    }

    @Test
    void testInvalidPassword() {
        String invalidPassword = "short";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> request.setPassword(invalidPassword));
        assertEquals("Password must be at least 9 characters long", exception.getMessage());
    }
}
