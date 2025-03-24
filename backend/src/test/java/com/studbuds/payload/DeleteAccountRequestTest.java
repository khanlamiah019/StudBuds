package com.studbuds.payload;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DeleteAccountRequestTest {

    @Test
    public void testValidEmail() {
        DeleteAccountRequest request = new DeleteAccountRequest();
        request.setEmail("test@cooper.edu");
        request.setFirebaseToken("dummy-token");
        assertEquals("test@cooper.edu", request.getEmail());
        assertEquals("dummy-token", request.getFirebaseToken());
    }

    @Test
    public void testInvalidEmail() {
        DeleteAccountRequest request = new DeleteAccountRequest();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            request.setEmail("test@gmail.com");
        });
        String expectedMessage = "Email must be a @cooper.edu address";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}