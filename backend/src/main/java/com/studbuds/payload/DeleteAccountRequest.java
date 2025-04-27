// src/main/java/com/studbuds/payload/DeleteAccountRequest.java
package com.studbuds.payload;

public class DeleteAccountRequest {
    private String firebaseToken;

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}