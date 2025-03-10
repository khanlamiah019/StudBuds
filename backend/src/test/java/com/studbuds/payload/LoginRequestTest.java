package com.studbuds.payload;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class LoginRequestTest {

    @Test
    public void testLoginRequestGettersAndSetters() {
        LoginRequest login = new LoginRequest();
        login.setEmail("student@cooper.edu");
        login.setPassword("password123");
        
        assertEquals("student@cooper.edu", login.getEmail());
        assertEquals("password123", login.getPassword());
    }
}
