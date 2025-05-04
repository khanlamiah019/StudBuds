package com.studbuds.service;

import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.repository.PreferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchingServiceTest {

    @Mock
    private PreferenceRepository prefRepo;

    @InjectMocks
    private MatchingService service;

    private User currentUser;
    private Preference currentPref;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking preference repository
        prefRepo = mock(PreferenceRepository.class);
        service = new MatchingService();
        service = spy(service);
        service.setPreferenceRepository(prefRepo);

        // Setup current user and their preference
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setMajor("Eng");
        currentUser.setYear("2025");

        currentPref = new Preference();
        currentPref.setUser(currentUser);
        currentPref.setAvailableDays("Monday,Tuesday");
        currentPref.setSubjectsToTeach("Math");
        currentPref.setSubjectsToLearn("Physics");
        currentUser.setPreference(currentPref);
    }

    @Test
    void whenPreferenceNull_thenEmpty() {
        User u = new User();
        u.setId(99L);
        u.setPreference(null);

        var results = service.findMatches(u);
        assertThat(results).isEmpty();
        verifyNoInteractions(prefRepo);
    }

    @Test
    void whenNarrowHasMatch_thenUseNarrow() {
        User other = new User();
        other.setId(2L);
        Preference p2 = new Preference();
        p2.setUser(other);
        p2.setAvailableDays("Tuesday,Wednesday");
        p2.setSubjectsToLearn("Math");
        p2.setSubjectsToTeach("Physics");

        when(prefRepo.findSimilarPreferences("Eng", "2025", 1L)).thenReturn(List.of(p2));

        var results = service.findMatches(currentUser);
        assertThat(results).hasSize(1);

        var dto = results.get(0);
        assertThat(dto.getMatchScore()).isEqualTo(3.5);
        assertThat(dto.getUser()).isEqualTo(other);
        assertThat(dto.getCommonDays()).containsExactly("tuesday");
        assertThat(dto.getCommonSubjects()).containsExactlyInAnyOrder("math", "physics");

        verify(prefRepo).findSimilarPreferences("Eng", "2025", 1L);
        verify(prefRepo, never()).findAll();
    }

    @Test
    void whenNarrowEmpty_thenUseFallback() {
        when(prefRepo.findSimilarPreferences(any(), any(), anyLong())).thenReturn(Collections.emptyList());

        User fb = new User();
        fb.setId(3L);
        Preference pf = new Preference();
        pf.setUser(fb);
        pf.setAvailableDays("Monday");
        pf.setSubjectsToLearn("Math,CS");
        pf.setSubjectsToTeach("Biology");

        when(prefRepo.findAll()).thenReturn(List.of(pf, currentPref));

        var results = service.findMatches(currentUser);
        assertThat(results).hasSize(1);

        var dto = results.get(0);
        assertThat(dto.getMatchScore()).isEqualTo(2.5);
        assertThat(dto.getUser()).isEqualTo(fb);

        verify(prefRepo).findSimilarPreferences("Eng", "2025", 1L);
        verify(prefRepo).findAll();
    }

    @Test
    void whenOnlyPartialSynergyNoDays_scoreBelowThreshold() {
        currentPref.setAvailableDays("");
        User other = new User();
        other.setId(4L);
        Preference p2 = new Preference();
        p2.setUser(other);
        p2.setAvailableDays("");
        p2.setSubjectsToLearn("Math");
        p2.setSubjectsToTeach("");

        when(prefRepo.findSimilarPreferences(any(), any(), anyLong())).thenReturn(List.of(p2));

        var results = service.findMatches(currentUser);
        assertThat(results).isEmpty();
    }
}
