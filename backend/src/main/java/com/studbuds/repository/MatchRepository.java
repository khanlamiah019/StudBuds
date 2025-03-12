package com.studbuds.repository;

import com.studbuds.model.Match;
import com.studbuds.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    
    @Query("SELECT m FROM Match m WHERE (m.user1 = :user1 AND m.user2 = :user2) OR (m.user1 = :user2 AND m.user2 = :user1)")
    Optional<Match> findExistingMatch(@Param("user1") User user1, @Param("user2") User user2);

    // New method to get all matches where the given user is involved.
    List<Match> findByUser1OrUser2(User user1, User user2);
    
    @Query("SELECT m FROM Match m WHERE m.user1 = :user OR m.user2 = :user")
    List<Match> findAllByUser(@Param("user") User user);
}
