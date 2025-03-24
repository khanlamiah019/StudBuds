package com.studbuds.payload;

public class LoginRequest {
    private String firebaseToken;

    public LoginRequest() {}

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}