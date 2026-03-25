package com.matchme.bio;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BioRepository extends JpaRepository<Bio, Long> {
    // Find bio by owning user id
    Optional<Bio> findByUserId(Long userId);
}
