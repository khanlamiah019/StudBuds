package com.studbuds.payload;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginRequestTest {

    @Test
    public void testLoginRequestGetterAndSetter() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setFirebaseToken("sample-firebase-token");
        assertEquals("sample-firebase-token", loginRequest.getFirebaseToken());
    }
}