package com.studbuds.payload;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SignupRequestTest {

    @Test
    public void testValidSignupRequest() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("Test User");
        signupRequest.setEmail("test@cooper.edu");
        signupRequest.setPassword("password123");
        signupRequest.setMajor("Computer Science");
        signupRequest.setYear("Junior");

        assertEquals("Test User", signupRequest.getName());
        assertEquals("test@cooper.edu", signupRequest.getEmail());
        assertEquals("password123", signupRequest.getPassword());
        assertEquals("Computer Science", signupRequest.getMajor());
        assertEquals("Junior", signupRequest.getYear());
    }

    @Test
    public void testInvalidEmailSignupRequest() {
        SignupRequest signupRequest = new SignupRequest();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            signupRequest.setEmail("test@gmail.com");
        });
        String expectedMessage = "Email must be a @cooper.edu address";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void testInvalidPasswordSignupRequest() {
        SignupRequest signupRequest = new SignupRequest();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            signupRequest.setPassword("short");
        });
        String expectedMessage = "Password must be at least 9 characters long";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}