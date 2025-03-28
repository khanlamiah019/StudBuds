package com.studbuds.repository;

import com.studbuds.model.Preference;
import com.studbuds.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Long> {
    Optional<Preference> findByUser(User user);

    @Query("SELECT p FROM Preference p WHERE p.user.major = :major AND p.user.year = :year AND p.user.id <> :userId")
    List<Preference> findSimilarPreferences(@Param("major") String major, @Param("year") String year, @Param("userId") Long userId);
}