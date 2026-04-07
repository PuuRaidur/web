package com.matchme.connection;

import com.matchme.connection.dto.ConnectionDetail;
import com.matchme.connection.dto.ConnectionRequestAction;
import com.matchme.connection.dto.ConnectionRequestCreate;
import com.matchme.connection.dto.ConnectionRequestResponse;
import com.matchme.connection.dto.ConnectionRequestsResponse;
import com.matchme.common.ProfileCompletionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ConnectionController {

    private final ConnectionRepository connectionRepository;
    private final ConnectionRequestRepository connectionRequestRepository;
    private final ProfileCompletionService profileCompletionService;

    public ConnectionController(ConnectionRepository connectionRepository,
                                ConnectionRequestRepository connectionRequestRepository,
                                ProfileCompletionService profileCompletionService) {
        this.connectionRepository = connectionRepository;
        this.connectionRequestRepository = connectionRequestRepository;
        this.profileCompletionService = profileCompletionService;
    }

    // Send a connection request
    @PostMapping("/connections/request")
    public ResponseEntity<ConnectionRequestResponse> sendConnectionRequest(Authentication authentication,
                                                                            @RequestBody ConnectionRequestCreate request) {
        Long senderId = (Long) authentication.getPrincipal();
        profileCompletionService.requireComplete(senderId);
        Long receiverId = request.receiverId;

        // Prevent sending request to yourself
        if (receiverId == null || senderId.equals(receiverId)) {
            return ResponseEntity.badRequest().build();
        }

        // Check if request already exists
        if (connectionRequestRepository.findBySenderIdAndReceiverId(senderId, receiverId).isPresent()) {
            return ResponseEntity.status(409).build(); // Conflict
        }

        // Check if already connected
        if (connectionRequestRepository.existsConnectionBetweenUsers(senderId, receiverId) > 0) {
            return ResponseEntity.status(409).build(); // Conflict
        }

        ConnectionRequest newRequest = new ConnectionRequest();
        newRequest.setSenderId(senderId);
        newRequest.setReceiverId(receiverId);
        ConnectionRequest saved = connectionRequestRepository.save(newRequest);

        return ResponseEntity.ok(new ConnectionRequestResponse(saved.getId()));
    }

    // View incoming requests
    @GetMapping("/connections/requests")
    public ResponseEntity<ConnectionRequestsResponse> getIncomingRequests(Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();
        profileCompletionService.requireComplete(currentUserId);

        List<Long> requesterIds = connectionRequestRepository.findIncomingRequests(currentUserId)
                .stream()
                .sorted(Comparator.comparing(ConnectionRequest::getCreatedAt).reversed())
                .map(ConnectionRequest::getSenderId)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ConnectionRequestsResponse(requesterIds));
    }

    // View outgoing requests
    @GetMapping("/connections/requests/outgoing")
    public ResponseEntity<ConnectionRequestsResponse> getOutgoingRequests(Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();
        profileCompletionService.requireComplete(currentUserId);

        List<Long> receiverIds = connectionRequestRepository.findOutgoingRequests(currentUserId)
                .stream()
                .sorted(Comparator.comparing(ConnectionRequest::getCreatedAt).reversed())
                .map(ConnectionRequest::getReceiverId)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ConnectionRequestsResponse(receiverIds));
    }

    // Accept a connection request
    @PostMapping("/connections/accept")
    @Transactional
    public ResponseEntity<Void> acceptConnectionRequest(Authentication authentication,
                                                         @RequestBody ConnectionRequestAction request) {
        Long currentUserId = (Long) authentication.getPrincipal();
        profileCompletionService.requireComplete(currentUserId);

        if (request == null || request.senderId == null) {
            return ResponseEntity.badRequest().build();
        }

        ConnectionRequest existing = connectionRequestRepository
                .findBySenderIdAndReceiverId(request.senderId, currentUserId)
                .orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        // Only the receiver can accept
        if (!existing.getReceiverId().equals(currentUserId)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        // Create the connection (ensure user1Id < user2Id for consistency)
        Connection connection = new Connection();
        if (existing.getSenderId() < existing.getReceiverId()) {
            connection.setUser1Id(existing.getSenderId());
            connection.setUser2Id(existing.getReceiverId());
        } else {
            connection.setUser1Id(existing.getReceiverId());
            connection.setUser2Id(existing.getSenderId());
        }
        connectionRepository.save(connection);

        // Delete the request
        connectionRequestRepository.delete(existing);

        return ResponseEntity.ok().build();
    }

    // Dismiss a connection request
    @PostMapping("/connections/dismiss")
    @Transactional
    public ResponseEntity<Void> dismissConnectionRequest(Authentication authentication,
                                                          @RequestBody ConnectionRequestAction request) {
        Long currentUserId = (Long) authentication.getPrincipal();
        profileCompletionService.requireComplete(currentUserId);

        if (request == null || request.senderId == null) {
            return ResponseEntity.badRequest().build();
        }

        ConnectionRequest existing = connectionRequestRepository
                .findBySenderIdAndReceiverId(request.senderId, currentUserId)
                .orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        // Only the receiver can dismiss
        if (!existing.getReceiverId().equals(currentUserId)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        // Delete the request
        connectionRequestRepository.delete(existing);

        return ResponseEntity.ok().build();
    }

    // Cancel an outgoing connection request
    @PostMapping("/connections/cancel")
    @Transactional
    public ResponseEntity<Void> cancelConnectionRequest(Authentication authentication,
                                                         @RequestBody ConnectionRequestCreate request) {
        Long currentUserId = (Long) authentication.getPrincipal();
        profileCompletionService.requireComplete(currentUserId);
        if (request == null || request.receiverId == null) {
            return ResponseEntity.badRequest().build();
        }

        ConnectionRequest existing = connectionRequestRepository
                .findBySenderIdAndReceiverId(currentUserId, request.receiverId)
                .orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        connectionRequestRepository.delete(existing);

        return ResponseEntity.ok().build();
    }

    // Get connected users with connection IDs for disconnecting
    @GetMapping("/connections")
    public ResponseEntity<List<ConnectionDetail>> getConnections(Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();
        profileCompletionService.requireComplete(currentUserId);

        List<Connection> connections = connectionRepository.findByUserId(currentUserId);

        // Extract the other user's ID and the connection entity ID
        List<ConnectionDetail> details = connections.stream()
                .map(connection -> {
                    Long otherId = connection.getUser1Id().equals(currentUserId)
                            ? connection.getUser2Id()
                            : connection.getUser1Id();
                    return new ConnectionDetail(connection.getId(), otherId);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(details);
    }

    // Phase 10: Disconnect from a user
    @DeleteMapping("/connections/{connectionId}")
    public ResponseEntity<Void> disconnect(Authentication authentication,
                                            @PathVariable Long connectionId) {
        Long currentUserId = (Long) authentication.getPrincipal();
        profileCompletionService.requireComplete(currentUserId);

        Connection connection = connectionRepository.findById(connectionId).orElse(null);
        if (connection == null) {
            return ResponseEntity.notFound().build();
        }

        // Ensure the user is part of this connection
        if (!connection.getUser1Id().equals(currentUserId) && !connection.getUser2Id().equals(currentUserId)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        connectionRepository.delete(connection);

        return ResponseEntity.ok().build();
    }
}
