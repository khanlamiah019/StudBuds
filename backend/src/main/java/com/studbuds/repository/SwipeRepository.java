package com.studbuds.repository;

import com.studbuds.model.Swipe;
import com.studbuds.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SwipeRepository extends JpaRepository<Swipe, Long> {
    List<Swipe> findByFromUser(User fromUser);
    List<Swipe> findByToUser(User toUser);
    List<Swipe> findByFromUserOrderByCreatedAtAsc(User fromUser); // ✅ for chronological sorting
}
