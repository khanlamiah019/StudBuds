package com.studbuds.repository;

import com.studbuds.model.Preference;
import com.studbuds.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Long> {
   Optional<Preference> findByUser(User user);
}