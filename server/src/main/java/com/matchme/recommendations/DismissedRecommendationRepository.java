package com.matchme.recommendations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DismissedRecommendationRepository extends JpaRepository<DismissedRecommendation, Long> {

    // Find all dismissed user IDs for a given user
    @Query("SELECT dr.dismissedUserId FROM DismissedRecommendation dr WHERE dr.userId = :userId")
    List<Long> findDismissedUserIdsByUserId(@Param("userId") Long userId);

    // Check if a user has already dismissed another user
    boolean existsByUserIdAndDismissedUserId(Long userId, Long dismissedUserId);
}
