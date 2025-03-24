package com.studbuds.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserGettersAndSetters() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@cooper.edu");
        user.setFirebaseUid("firebase-uid-123");
        LocalDateTime createdAt = LocalDateTime.now();
        user.setCreatedAt(createdAt);

        Preference preference = new Preference();
        preference.setId(10L);
        user.setPreference(preference);

        assertEquals(1L, user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@cooper.edu", user.getEmail());
        assertEquals("firebase-uid-123", user.getFirebaseUid());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(preference, user.getPreference());
    }
}