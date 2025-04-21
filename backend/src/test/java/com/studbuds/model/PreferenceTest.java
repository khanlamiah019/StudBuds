package com.studbuds.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PreferenceTest {

    @Test
    void gettersAndSetters_workAsExpected() {
        Preference pref = new Preference();

        // id
        pref.setId(42L);
        assertThat(pref.getId()).isEqualTo(42L);

        // user
        User u = new User();
        u.setId(7L);
        pref.setUser(u);
        assertThat(pref.getUser()).isSameAs(u);

        // availableDays
        pref.setAvailableDays("Mon, Wed ,Fri");
        assertThat(pref.getAvailableDays()).isEqualTo("Mon, Wed ,Fri");

        // subjectsToLearn
        pref.setSubjectsToLearn("math, physics");
        assertThat(pref.getSubjectsToLearn()).isEqualTo("math, physics");

        // subjectsToTeach
        pref.setSubjectsToTeach("chemistry");
        assertThat(pref.getSubjectsToTeach()).isEqualTo("chemistry");
    }
}
