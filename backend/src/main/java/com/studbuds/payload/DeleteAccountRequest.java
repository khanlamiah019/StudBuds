package com.studbuds.payload;

public class DeleteAccountRequest {
    private String email;
    private String firebaseToken; // Optional if you want to include the token from the client.

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getFirebaseToken() {
        return firebaseToken;
    }
    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}