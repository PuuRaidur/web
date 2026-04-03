package com.matchme.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Find all users except the one with the given id (for recommendations)
    List<User> findAllByIdNot(Long excludeId);

    // Find all users except the given id and not in the list of excluded ids (for recommendations with dismissals)
    List<User> findAllByIdNotAndIdNotIn(Long excludeId, List<Long> excludedIds);
}
