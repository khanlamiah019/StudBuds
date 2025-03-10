package com.studbuds.payload;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class SignupRequestTest {

    @Test
    public void testValidSignupRequest() {
        SignupRequest signup = new SignupRequest();
        signup.setName("George Washington");
        signup.setEmail("george.washington@cooper.edu");
        signup.setPassword("password123"); // valid: >=9 characters
        signup.setMajor("Electrical Engineering");
        signup.setYear("2025");

        assertEquals("George Washington", signup.getName());
        assertEquals("george.washington@cooper.edu", signup.getEmail());
        assertEquals("password123", signup.getPassword());
        assertEquals("Electrical Engineering", signup.getMajor());
        assertEquals("2025", signup.getYear());
    }

    @Test
    public void testInvalidEmail() {
        SignupRequest signup = new SignupRequest();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            signup.setEmail("george.washington@gmail.com");
        });
        assertEquals("Email must be a @cooper.edu address", exception.getMessage());
    }

    @Test
    public void testInvalidPassword() {
        SignupRequest signup = new SignupRequest();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            signup.setPassword("short"); // less than 9 characters
        });
        assertEquals("Password must be at least 9 characters long", exception.getMessage());
    }
}
