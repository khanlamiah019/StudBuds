package com.studbuds.service;

import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.repository.PreferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private PreferenceRepository preferenceRepository;

    @InjectMocks
    private MatchingService matchingService;

    private User currentUser;
    private User candidateUser1;
    private User candidateUser2;

    private Preference currentPref;
    private Preference candidatePref1;
    private Preference candidatePref2;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setMajor("Electrical Engineering");
        currentUser.setYear("3");

        candidateUser1 = new User();
        candidateUser1.setId(2L);
        candidateUser1.setMajor("Electrical Engineering");
        candidateUser1.setYear("3");

        candidateUser2 = new User();
        candidateUser2.setId(3L);
        candidateUser2.setMajor("Computer Science");
        candidateUser2.setYear("3");

        currentPref = new Preference();
        currentPref.setUser(currentUser);
        currentPref.setAvailableDays("Monday,Wednesday,Friday");
        currentPref.setSubjectsToTeach("Math,Physics");
        currentPref.setSubjectsToLearn("Programming");

        candidatePref1 = new Preference();
        candidatePref1.setUser(candidateUser1);
        candidatePref1.setAvailableDays("Monday,Tuesday,Friday");
        candidatePref1.setSubjectsToTeach("Programming");
        candidatePref1.setSubjectsToLearn("Math");

        candidatePref2 = new Preference();
        candidatePref2.setUser(candidateUser2);
        candidatePref2.setAvailableDays("Thursday,Friday");
        candidatePref2.setSubjectsToTeach("Biology");
        candidatePref2.setSubjectsToLearn("Chemistry");

        currentUser.setPreference(currentPref);
        candidateUser1.setPreference(candidatePref1);
        candidateUser2.setPreference(candidatePref2);
    }

    @Test
    void testFindMatches_withSimilarMajorAndYear() {
        when(preferenceRepository.findSimilarPreferences(
                currentUser.getMajor(), currentUser.getYear(), currentUser.getId()
        )).thenReturn(Arrays.asList(candidatePref1));

        List<MatchingService.MatchingResultDTO> matches = matchingService.findMatches(currentUser);

        assertFalse(matches.isEmpty());
        assertEquals(1, matches.size());
        assertEquals(candidateUser1.getId(), matches.get(0).getUser().getId());

        verify(preferenceRepository, times(1)).findSimilarPreferences(
                currentUser.getMajor(), currentUser.getYear(), currentUser.getId()
        );
    }

    @Test
    void testFindMatches_fallbackToAllUsers() {
        when(preferenceRepository.findSimilarPreferences(
                currentUser.getMajor(), currentUser.getYear(), currentUser.getId()
        )).thenReturn(Collections.emptyList());

        when(preferenceRepository.findAll()).thenReturn(Arrays.asList(candidatePref1, candidatePref2));

        List<MatchingService.MatchingResultDTO> matches = matchingService.findMatches(currentUser);

        assertEquals(1, matches.size());
        assertEquals(candidateUser1.getId(), matches.get(0).getUser().getId());

        verify(preferenceRepository, times(1)).findSimilarPreferences(
                currentUser.getMajor(), currentUser.getYear(), currentUser.getId()
        );
        verify(preferenceRepository, times(1)).findAll();
    }

    @Test
    void testFindMatches_noValidMatches() {
        when(preferenceRepository.findSimilarPreferences(
                currentUser.getMajor(), currentUser.getYear(), currentUser.getId()
        )).thenReturn(Collections.emptyList());

        when(preferenceRepository.findAll()).thenReturn(Collections.emptyList());

        List<MatchingService.MatchingResultDTO> matches = matchingService.findMatches(currentUser);

        assertTrue(matches.isEmpty());

        verify(preferenceRepository, times(1)).findSimilarPreferences(
                currentUser.getMajor(), currentUser.getYear(), currentUser.getId()
        );
        verify(preferenceRepository, times(1)).findAll();
    }
}
