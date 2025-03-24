package com.studbuds.controller;

import com.studbuds.model.Match;
import com.studbuds.model.Preference;
import com.studbuds.model.Swipe;
import com.studbuds.model.User;
import com.studbuds.repository.MatchRepository;
import com.studbuds.repository.SwipeRepository;
import com.studbuds.repository.UserRepository;
import com.studbuds.service.MatchingService;
import com.studbuds.service.MatchingService.MatchingResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MatchingControllerTest {

    @InjectMocks
    private MatchingController matchingController;

    @Mock
    private MatchingService matchingService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private SwipeRepository swipeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindMatches_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = matchingController.findMatches(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found.", response.getBody());
    }

    @Test
    void testFindMatches_NoPreference() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@cooper.edu");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        ResponseEntity<?> response = matchingController.findMatches(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("No preferences set for this user.", response.getBody());
    }

    @Test
    void testFindMatches_Success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@cooper.edu");
        Preference preference = new Preference();
        preference.setAvailableDays("Monday,Tuesday");
        user.setPreference(preference);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<MatchingResultDTO> mockResults = new ArrayList<>();
        MatchingResultDTO dto = new MatchingResultDTO();
        dto.setMatchScore(2.5);
        dto.setUser(new User());
        dto.setCommonDays(Arrays.asList("monday"));
        dto.setCommonSubjects(Arrays.asList("math"));
        mockResults.add(dto);
        when(matchingService.findMatches(user)).thenReturn(mockResults);

        ResponseEntity<?> response = matchingController.findMatches(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResults, response.getBody());
    }

    @Test
    void testSwipe_NoReciprocalSwipe() {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@cooper.edu");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@cooper.edu");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        // No existing swipe or reciprocal swipe
        when(swipeRepository.findAll()).thenReturn(new ArrayList<>());

        ResponseEntity<?> response = matchingController.swipe(1L, 2L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Swipe recorded; waiting for mutual interest.", response.getBody());
    }

    @Test
    void testSwipe_WithReciprocalSwipe() {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@cooper.edu");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@cooper.edu");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        // Simulate reciprocal swipe exists from user2 to user1
        Swipe reciprocalSwipe = new Swipe();
        reciprocalSwipe.setFromUser(user2);
        reciprocalSwipe.setToUser(user1);

        List<Swipe> swipes = Arrays.asList(reciprocalSwipe);
        when(swipeRepository.findAll()).thenReturn(swipes);
        when(matchRepository.findExistingMatch(user1, user2)).thenReturn(Optional.empty());

        doNothing().when(swipeRepository).delete(reciprocalSwipe);
        Match match = new Match();
        match.setUser1(user1);
        match.setUser2(user2);
        match.setMatchDate(LocalDateTime.now());
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        ResponseEntity<?> response = matchingController.swipe(1L, 2L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Match);
    }

    @Test
    void testProfileMatches_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = matchingController.getProfileMatches(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found.", response.getBody());
    }

    @Test
    void testProfileMatches_Success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@cooper.edu");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Simulate confirmed matches
        User matchUser = new User();
        matchUser.setId(2L);
        matchUser.setEmail("match@cooper.edu");
        Match match = new Match();
        match.setUser1(user);
        match.setUser2(matchUser);
        List<Match> matches = Arrays.asList(match);
        when(matchRepository.findByUser1OrUser2(user, user)).thenReturn(matches);

        // Simulate pending swipes
        Swipe swipe = new Swipe();
        swipe.setFromUser(user);
        swipe.setToUser(matchUser);
        List<Swipe> swipes = Arrays.asList(swipe);
        when(swipeRepository.findByFromUser(user)).thenReturn(swipes);

        ResponseEntity<?> response = matchingController.getProfileMatches(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> result = (Map<?, ?>) response.getBody();
        assertTrue(result.containsKey("confirmedMatches"));
        assertTrue(result.containsKey("pendingMatches"));
    }
}