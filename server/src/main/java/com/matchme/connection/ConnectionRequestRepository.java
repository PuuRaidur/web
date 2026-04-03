package com.matchme.connection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Long> {

    // Find all incoming requests for a user (where user is the receiver)
    @Query("SELECT cr FROM ConnectionRequest cr WHERE cr.receiverId = :userId")
    List<ConnectionRequest> findIncomingRequests(@Param("userId") Long userId);

    // Find all outgoing requests for a user (where user is the sender)
    @Query("SELECT cr FROM ConnectionRequest cr WHERE cr.senderId = :userId")
    List<ConnectionRequest> findOutgoingRequests(@Param("userId") Long userId);

    // Check if a request already exists
    Optional<ConnectionRequest> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    // Check if users already have a connection
    @Query("SELECT COUNT(c) FROM Connection c WHERE (c.user1Id = :user1Id AND c.user2Id = :user2Id) OR (c.user1Id = :user2Id AND c.user2Id = :user1Id)")
    long existsConnectionBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}
