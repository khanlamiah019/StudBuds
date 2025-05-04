package com.studbuds.repository;

import com.studbuds.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByFirebaseUid(String firebaseUid);
    boolean existsByEmailIgnoreCase(String email); // ✅ New for cleaner logic
}
