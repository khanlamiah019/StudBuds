package com.studbuds.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class MatchTest {

    @Test
    public void testMatchGettersAndSetters() {
        Match match = new Match();
        match.setId(100L);

        User user1 = new User();
        user1.setId(1L);
        user1.setName("User One");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User Two");

        match.setUser1(user1);
        match.setUser2(user2);

        LocalDateTime now = LocalDateTime.now();
        match.setMatchDate(now);

        assertEquals(100L, match.getId());
        assertEquals(user1, match.getUser1());
        assertEquals(user2, match.getUser2());
        assertEquals(now, match.getMatchDate());
    }
}
