package com.studbuds.service;

import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.repository.PreferenceRepository;
import com.studbuds.service.MatchingService.MatchingResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchingServiceTest {

    @InjectMocks
    private MatchingService matchingService;

    @Mock
    private PreferenceRepository preferenceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindMatches_NoPreference() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@cooper.edu");
        user.setPreference(null);
        List<MatchingResultDTO> results = matchingService.findMatches(user);
        assertTrue(results.isEmpty());
    }

    @Test
    void testFindMatches_NarrowCandidates() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@cooper.edu");
        Preference pref = new Preference();
        pref.setMajor("CS");
        pref.setYear("Sophomore");
        pref.setAvailableDays("Monday,Tuesday");
        pref.setSubjectsToTeach("Math");
        pref.setSubjectsToLearn("Science");
        user.setPreference(pref);

        Preference candidatePref = new Preference();
        candidatePref.setMajor("CS");
        candidatePref.setYear("Sophomore");
        candidatePref.setAvailableDays("Monday,Wednesday");
        candidatePref.setSubjectsToTeach("Science");
        candidatePref.setSubjectsToLearn("Math");
        User candidateUser = new User();
        candidateUser.setId(2L);
        candidateUser.setEmail("candidate@cooper.edu");
        candidatePref.setUser(candidateUser);

        when(preferenceRepository.findSimilarPreferences("CS", "Sophomore", 1L))
                .thenReturn(Arrays.asList(candidatePref));

        List<MatchingResultDTO> results = matchingService.findMatches(user);
        assertFalse(results.isEmpty());
        MatchingResultDTO dto = results.get(0);
        assertEquals(candidateUser, dto.getUser());
        assertTrue(dto.getCommonDays().contains("monday"));
        assertTrue(dto.getCommonSubjects().contains("math"));
        assertTrue(dto.getCommonSubjects().contains("science"));
    }

    @Test
    void testFindMatches_FallbackCandidates() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@cooper.edu");
        Preference pref = new Preference();
        pref.setMajor("CS");
        pref.setYear("Sophomore");
        pref.setAvailableDays("Monday");
        pref.setSubjectsToTeach("Math");
        pref.setSubjectsToLearn("Science");
        user.setPreference(pref);

        // Return empty for narrow candidates
        when(preferenceRepository.findSimilarPreferences("CS", "Sophomore", 1L))
                .thenReturn(new ArrayList<>());
        // For fallback, simulate a list with one candidate.
        Preference candidatePref = new Preference();
        candidatePref.setMajor("CS");
        candidatePref.setYear("Sophomore");
        candidatePref.setAvailableDays("Monday");
        candidatePref.setSubjectsToTeach("Science");
        candidatePref.setSubjectsToLearn("Math");
        User candidateUser = new User();
        candidateUser.setId(2L);
        candidateUser.setEmail("candidate@cooper.edu");
        candidatePref.setUser(candidateUser);

        when(preferenceRepository.findAll()).thenReturn(Arrays.asList(candidatePref));

        List<MatchingResultDTO> results = matchingService.findMatches(user);
        assertFalse(results.isEmpty());
        MatchingResultDTO dto = results.get(0);
        assertEquals(candidateUser, dto.getUser());
    }
}