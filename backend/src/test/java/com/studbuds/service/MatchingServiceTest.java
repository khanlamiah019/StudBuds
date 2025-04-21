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

        // build currentUser + preference
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
        // create an “other” user whose preference overlaps fully
        User other = new User();
        other.setId(2L);
        Preference p2 = new Preference();
        p2.setUser(other);
        p2.setAvailableDays("Tuesday,Wednesday");
        p2.setSubjectsToLearn("Math");    // other wants to learn Math
        p2.setSubjectsToTeach("Physics"); // other can teach Physics

        // narrow query returns exactly that one
        when(prefRepo.findSimilarPreferences("Eng", "2025", 1L))
            .thenReturn(List.of(p2));

        var results = service.findMatches(currentUser);
        // should return one result
        assertThat(results).hasSize(1);

        MatchingService.MatchingResultDTO dto = results.get(0);
        // score = 2.0 (days) + 0.5 + 0.5 + 0.5 = 3.5
        assertThat(dto.getMatchScore()).isEqualTo(3.5);
        assertThat(dto.getUser()).isEqualTo(other);

        // commonDays → “tuesday”
        assertThat(dto.getCommonDays()).containsExactly("tuesday");

        // commonSubjects → both Math & Physics
        assertThat(dto.getCommonSubjects())
            .containsExactlyInAnyOrder("math", "physics");

        verify(prefRepo).findSimilarPreferences("Eng", "2025", 1L);
        verify(prefRepo, never()).findAll();
    }

    @Test
    void whenNarrowEmpty_thenUseFallback() {
        // no narrow matches
        when(prefRepo.findSimilarPreferences(any(), any(), anyLong()))
            .thenReturn(Collections.emptyList());

        // fallback list includes one good candidate
        User fb = new User();
        fb.setId(3L);
        Preference pf = new Preference();
        pf.setUser(fb);
        pf.setAvailableDays("Monday");          // common day
        pf.setSubjectsToLearn("Math,CS");       // wants to learn Math
        pf.setSubjectsToTeach("Biology");       // irrelevant

        // also include currentUser in findAll to test filtering
        when(prefRepo.findAll())
            .thenReturn(List.of(pf, currentPref));

        var results = service.findMatches(currentUser);

        // should return only pf
        assertThat(results).hasSize(1);
        var dto = results.get(0);
        // score = 2.0 (commonDays) + 0.5 (p1TeachesP2: Math) = 2.5
        assertThat(dto.getMatchScore()).isEqualTo(2.5);
        assertThat(dto.getUser()).isEqualTo(fb);

        verify(prefRepo).findSimilarPreferences("Eng", "2025", 1L);
        verify(prefRepo).findAll();
    }

    @Test
    void whenOnlyPartialSynergyNoDays_scoreBelowThreshold() {
        // set current days to none
        currentPref.setAvailableDays("");
        // create candidate with only one subject overlap
        User other = new User();
        other.setId(4L);
        Preference p2 = new Preference();
        p2.setUser(other);
        p2.setAvailableDays("");         // no common days
        p2.setSubjectsToLearn("Math");    // overlap in teach→learn
        p2.setSubjectsToTeach("");       

        when(prefRepo.findSimilarPreferences(any(), any(), anyLong()))
            .thenReturn(List.of(p2));

        var results = service.findMatches(currentUser);
        // score=0.5<1 → filtered out
        assertThat(results).isEmpty();
    }
}
