package com.matchme.connection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    // Find all connections for a user (user can be either user1 or user2)
    @Query("SELECT c FROM Connection c WHERE c.user1Id = :userId OR c.user2Id = :userId")
    List<Connection> findByUserId(@Param("userId") Long userId);

    // Check if two users are already connected in either direction.
    @Query("""
            SELECT (COUNT(c) > 0) FROM Connection c
            WHERE (c.user1Id = :userA AND c.user2Id = :userB)
               OR (c.user1Id = :userB AND c.user2Id = :userA)
            """)
    boolean existsBetweenUsers(@Param("userA") Long userA, @Param("userB") Long userB);

    @Query("""
            SELECT c FROM Connection c
            WHERE (c.user1Id = :userA AND c.user2Id = :userB)
               OR (c.user1Id = :userB AND c.user2Id = :userA)
            """)
    Optional<Connection> findBetweenUsers(@Param("userA") Long userA, @Param("userB") Long userB);
}
