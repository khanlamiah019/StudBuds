package com.studbuds.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studbuds.model.Match;
import com.studbuds.model.Swipe;
import com.studbuds.model.User;
import com.studbuds.repository.MatchRepository;
import com.studbuds.repository.SwipeRepository;
import com.studbuds.repository.UserRepository;
import com.studbuds.service.MatchingService;
import com.studbuds.service.MatchingService.MatchingResultDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MatchingController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security filters (e.g. CSRF)
public class MatchingControllerTest {

    @Autowired
    private MockMvc mockMvc;
        
    @MockBean
    private MatchingService matchingService;
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private MatchRepository matchRepository;
    
    @MockBean
    private SwipeRepository swipeRepository;
    
    // ===== Tests for GET /api/matches/find/{userId} =====
    
    @Test
    public void testFindMatches_UserNotFound() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/matches/find/1"))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("User not found.")));
    }
    
    @Test
    public void testFindMatches_NoPreferencesSet() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("user1@domain.com");
        // Explicitly set no preference.
        user.setPreference(null);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        mockMvc.perform(get("/api/matches/find/1"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("No preferences set for this user.")));
    }
    
    @Test
    public void testFindMatches_WithPreferences() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("user1@domain.com");
        // Set a dummy non-null preference.
        user.setPreference(new com.studbuds.model.Preference());
        
        List<MatchingResultDTO> dummyMatches = new ArrayList<>();
        MatchingResultDTO dto = new MatchingResultDTO();
        User matchedUser = new User();
        matchedUser.setId(2L);
        matchedUser.setEmail("user2@domain.com");
        dto.setUser(matchedUser);
        dto.setCommonDays(Arrays.asList("monday", "wednesday"));
        dto.setCommonSubjects(Arrays.asList("math", "science"));
        dto.setMatchScore(2.5);
        dummyMatches.add(dto);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(matchingService.findMatches(user)).thenReturn(dummyMatches);
        
        mockMvc.perform(get("/api/matches/find/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].user.id").value(2))
            .andExpect(jsonPath("$[0].user.email").value("user2@domain.com"));
    }
    
    // ===== Tests for POST /api/matches/swipe =====
    
    @Test
    public void testSwipe_UserNotFound() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        
        mockMvc.perform(post("/api/matches/swipe")
                .param("user1Id", "1")
                .param("user2Id", "2"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("One or both users not found.")));
        
        verify(userRepository, times(1)).findById(1L);
    }
    
    @Test
    public void testSwipe_AlreadyMatched() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@domain.com");
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@domain.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        // Simulate that a confirmed match already exists.
        when(matchRepository.findExistingMatch(user1, user2)).thenReturn(Optional.of(new Match()));
        
        mockMvc.perform(post("/api/matches/swipe")
                .param("user1Id", "1")
                .param("user2Id", "2"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("These users have already matched.")));
        
        verify(matchRepository, times(1)).findExistingMatch(user1, user2);
    }
    
    @Test
    public void testSwipe_AlreadySwiped() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@domain.com");
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@domain.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(matchRepository.findExistingMatch(user1, user2)).thenReturn(Optional.empty());
        // Simulate that user1 already swiped on user2.
        Swipe swipe = new Swipe();
        swipe.setFromUser(user1);
        swipe.setToUser(user2);
        when(swipeRepository.findAll()).thenReturn(Collections.singletonList(swipe));
        
        mockMvc.perform(post("/api/matches/swipe")
                .param("user1Id", "1")
                .param("user2Id", "2"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("You have already swiped on this user.")));
    }
    
    @Test
    public void testSwipe_ReciprocalSwipeCreatesMatch() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@domain.com");
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@domain.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(matchRepository.findExistingMatch(user1, user2)).thenReturn(Optional.empty());
        
        // Simulate that user2 has already swiped on user1.
        Swipe reciprocalSwipe = new Swipe();
        reciprocalSwipe.setFromUser(user2);
        reciprocalSwipe.setToUser(user1);
        when(swipeRepository.findAll()).thenReturn(Collections.singletonList(reciprocalSwipe));
        
        // Instead of returning a pre-built savedMatch, use thenAnswer to set the id.
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> {
            Match m = invocation.getArgument(0);
            m.setId(100L); // Set the id here.
            return m;
        });
        
        mockMvc.perform(post("/api/matches/swipe")
                .param("user1Id", "1")
                .param("user2Id", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(100))
            .andExpect(jsonPath("$.user1.id").value(1))
            .andExpect(jsonPath("$.user2.id").value(2));
        
        verify(swipeRepository, times(1)).delete(reciprocalSwipe);
        verify(matchRepository, times(1)).save(any(Match.class));
    }
    
    
    // ===== Tests for GET /api/matches/profile/{userId} =====
    
    @Test
    public void testGetProfileMatches_UserNotFound() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/matches/profile/1"))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("User not found.")));
    }
    
    @Test
    public void testGetProfileMatches_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("user1@domain.com");
        
        // Create a confirmed match where 'user' is user1.
        User matchedUser = new User();
        matchedUser.setId(2L);
        matchedUser.setEmail("user2@domain.com");
        Match match = new Match();
        match.setId(10L);
        match.setUser1(user);
        match.setUser2(matchedUser);
        List<Match> confirmedMatches = Collections.singletonList(match);
        when(matchRepository.findByUser1OrUser2(user, user)).thenReturn(confirmedMatches);
        
        // Create a pending swipe.
        User pendingUser = new User();
        pendingUser.setId(3L);
        pendingUser.setEmail("user3@domain.com");
        Swipe pendingSwipe = new Swipe();
        pendingSwipe.setFromUser(user);
        pendingSwipe.setToUser(pendingUser);
        List<Swipe> pendingSwipes = Collections.singletonList(pendingSwipe);
        when(swipeRepository.findByFromUser(user)).thenReturn(pendingSwipes);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        mockMvc.perform(get("/api/matches/profile/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.confirmedMatches", hasSize(1)))
            .andExpect(jsonPath("$.pendingMatches", hasSize(1)));
    }
}
