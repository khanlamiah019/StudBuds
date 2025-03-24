package com.studbuds.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PreferenceTest {

    @Test
    public void testPreferenceGettersAndSetters() {
        Preference pref = new Preference();
        pref.setId(20L);
        
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        pref.setUser(user);
        
        pref.setAvailableDays("Monday,Tuesday");
        pref.setSubjectsToLearn("Math,Science");
        pref.setSubjectsToTeach("History,English");
        pref.setMajor("Computer Science");
        pref.setYear("Sophomore");
        
        assertEquals(20L, pref.getId());
        assertEquals(user, pref.getUser());
        assertEquals("Monday,Tuesday", pref.getAvailableDays());
        assertEquals("Math,Science", pref.getSubjectsToLearn());
        assertEquals("History,English", pref.getSubjectsToTeach());
        assertEquals("Computer Science", pref.getMajor());
        assertEquals("Sophomore", pref.getYear());
    }
}