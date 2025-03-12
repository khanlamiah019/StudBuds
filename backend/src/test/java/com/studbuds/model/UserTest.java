package com.studbuds.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import java.time.LocalDateTime;

class UserTest {

    private User user;
    private Preference mockPreference;

    @BeforeEach
    void setUp() {
        user = new User();
        mockPreference = Mockito.mock(Preference.class);
    }

    @Test
    void testIdSetterAndGetter() {
        Long id = 1L;
        user.setId(id);
        assertEquals(id, user.getId());
    }

    @Test
    void testEmailSetterAndGetter() {
        String email = "test@example.com";
        user.setEmail(email);
        assertEquals(email, user.getEmail());
    }

    @Test
    void testPasswordSetterAndGetter() {
        String password = "securepassword";
        user.setPassword(password);
        assertEquals(password, user.getPassword());
    }

    @Test
    void testMajorSetterAndGetter() {
        String major = "Computer Science";
        user.setMajor(major);
        assertEquals(major, user.getMajor());
    }

    @Test
    void testYearSetterAndGetter() {
        String year = "Senior";
        user.setYear(year);
        assertEquals(year, user.getYear());
    }

    @Test
    void testCreatedAtSetterAndGetter() {
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        assertEquals(now, user.getCreatedAt());
    }

    @Test
    void testPreferenceSetterAndGetter() {
        user.setPreference(mockPreference);
        assertEquals(mockPreference, user.getPreference());
    }
}