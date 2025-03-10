package com.studbuds.payload;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
    }

    @Test
    void testEmailNotFound() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            loginRequest.setEmail("unregistered@cooper.edu"));
        assertEquals("Email not found. Please sign up first.", exception.getMessage());
    }

    @Test
    void testInvalidEmail() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            loginRequest.setEmail("george.washington@gmail.com");
        });
        assertEquals("Email must be a @cooper.edu address", exception.getMessage());
    }

    @Test
    void testUnregisteredEmail() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            loginRequest.setEmail("unknown@cooper.edu");
        });
        assertEquals("Email not found. Please sign up first.", exception.getMessage());
    }

    @Test
    void testValidPassword() {
        loginRequest.setPassword("password123");
        assertEquals("password123", loginRequest.getPassword());
    }

    @Test
    void testShortPassword() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            loginRequest.setPassword("short");
        });
        assertEquals("Password must be at least 9 characters long", exception.getMessage());
    }
}
