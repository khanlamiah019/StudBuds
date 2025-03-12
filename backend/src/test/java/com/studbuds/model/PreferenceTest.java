package com.studbuds.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

class PreferenceTest {

    private Preference preference;
    private User mockUser;

    @BeforeEach
    void setUp() {
        preference = new Preference();
        mockUser = Mockito.mock(User.class);
    }

    @Test
    void testIdSetterAndGetter() {
        Long id = 1L;
        preference.setId(id);
        assertEquals(id, preference.getId());
    }

    @Test
    void testUserSetterAndGetter() {
        preference.setUser(mockUser);
        assertEquals(mockUser, preference.getUser());
    }

    @Test
    void testAvailableDaysSetterAndGetter() {
        String availableDays = "Monday, Wednesday, Friday";
        preference.setAvailableDays(availableDays);
        assertEquals(availableDays, preference.getAvailableDays());
    }

    @Test
    void testSubjectsToLearnSetterAndGetter() {
        String subjectsToLearn = "Math, Science";
        preference.setSubjectsToLearn(subjectsToLearn);
        assertEquals(subjectsToLearn, preference.getSubjectsToLearn());
    }

    @Test
    void testSubjectsToTeachSetterAndGetter() {
        String subjectsToTeach = "English, History";
        preference.setSubjectsToTeach(subjectsToTeach);
        assertEquals(subjectsToTeach, preference.getSubjectsToTeach());
    }
}
