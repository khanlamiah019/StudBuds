package com.studbuds.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class MatchTest {
    
    @Test
    void testMatchEntity() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        
        Match match = new Match();
        match.setId(100L);
        match.setUser1(user1);
        match.setUser2(user2);
        LocalDateTime testDate = LocalDateTime.of(2025, 3, 12, 12, 0);
        match.setMatchDate(testDate);
        
        assertEquals(100L, match.getId());
        assertEquals(user1, match.getUser1());
        assertEquals(user2, match.getUser2());
        assertEquals(testDate, match.getMatchDate());
    }
    
    @Test
    void testDefaultMatchDate() {
        Match match = new Match();
        assertNotNull(match.getMatchDate(), "Match date should not be null");
    }
}
