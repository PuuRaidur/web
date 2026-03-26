package com.matchme.connection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    // Find all connections for a user (user can be either user1 or user2)
    @Query("SELECT c FROM Connection c WHERE c.user1Id = :userId OR c.user2Id = :userId")
    List<Connection> findByUserId(@Param("userId") Long userId);
}
