package com.studbuds.repository;

import com.studbuds.model.Match;
import com.studbuds.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByUser1OrUser2(User user1, User user2);
}
