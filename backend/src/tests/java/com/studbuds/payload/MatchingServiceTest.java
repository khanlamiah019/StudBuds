package com.studbuds.service;

import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.repository.PreferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MatchingServiceTest {

    @Mock
    private PreferenceRepository preferenceRepository;

    @InjectMocks
    private MatchingService matchingService;

    private User currentUser;
    private Preference currentPref;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a user
        currentUser = new User();
        currentUser.setId(1L);

        // Create preferences for the current user
        currentPref = new Preference();
        currentPref.setAvailableDays("Monday, Wednesday, Friday");
        currentPref.setSubjectsToTeach("Math, Physics");
        currentPref.setSubjectsToLearn("Chemistry, Biology");
        currentPref.setUser(currentUser);

        currentUser.setPreference(currentPref);
    }

    @Test
    void testFindMatches_withValidMatches() {
        // Prepare mock data
        Preference otherPref = new Preference();
        User otherUser = new User();
        otherUser.setId(2L);
        otherPref.setUser(otherUser);
        otherPref.setAvailableDays("Monday, Tuesday");
        otherPref.setSubjectsToTeach("Biology, Chemistry");
        otherPref.setSubjectsToLearn("Math, Physics");

        List<Preference> preferences = Arrays.asList(currentPref, otherPref);
        when(preferenceRepository.findAll()).thenReturn(preferences);

        // Run the method
        List<MatchingService.MatchingResultDTO> matches = matchingService.findMatches(currentUser);

        // Check that the match is found
        assertEquals(1, matches.size());
        MatchingService.MatchingResultDTO match = matches.get(0);

        // Check if the match has common days and subjects
        assertTrue(match.getCommonDays().contains("Monday"));
        assertTrue(match.getCommonSubjects().contains("Math"));
        assertTrue(match.getCommonSubjects().contains("Physics"));
        assertEquals(3, match.getMatchScore(), 0.01);
    }

    @Test
    void testFindMatches_withNoValidMatches() {
        // Prepare mock data where no common days or subjects
        Preference otherPref = new Preference();
        User otherUser = new User();
        otherUser.setId(2L);
        otherPref.setUser(otherUser);
        otherPref.setAvailableDays("Tuesday, Thursday");
        otherPref.setSubjectsToTeach("Biology, Chemistry");
        otherPref.setSubjectsToLearn("History, Geography");

        List<Preference> preferences = Arrays.asList(currentPref, otherPref);
        when(preferenceRepository.findAll()).thenReturn(preferences);

        // Run the method
        List<MatchingService.MatchingResultDTO> matches = matchingService.findMatches(currentUser);

        // Check that there are no valid matches, and one match with score 0 is returned
        assertEquals(1, matches.size());
        MatchingService.MatchingResultDTO match = matches.get(0);
        assertEquals(0, match.getMatchScore(), 0.01);
    }

    @Test
    void testFindMatches_withNoPreferences() {
        // Prepare mock data with no preferences for the user
        currentUser.setPreference(null);
        List<Preference> preferences = Arrays.asList();
        when(preferenceRepository.findAll()).thenReturn(preferences);

        // Run the method
        List<MatchingService.MatchingResultDTO> matches = matchingService.findMatches(currentUser);

        // Check that no matches are returned
        assertTrue(matches.isEmpty());
    }
}
