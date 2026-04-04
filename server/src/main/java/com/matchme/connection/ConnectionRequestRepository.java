package com.matchme.connection;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Long> {

    // Find all incoming requests for a user.
    List<ConnectionRequest> findByReceiverId(Long receiverId);

    // Find all outgoing requests for a user.
    List<ConnectionRequest> findBySenderId(Long senderId);

    // Check if a request already exists.
    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    // Find a specific request by sender/receiver.
    Optional<ConnectionRequest> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    // Delete a specific request by sender/receiver.
    void deleteBySenderIdAndReceiverId(Long senderId, Long receiverId);
}
