package com.matchme.profile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository  extends JpaRepository<Profile, Long> {
    // Find profile by owning id
    Optional<Profile> findByUserId(Long userId);
}
