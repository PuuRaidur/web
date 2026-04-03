package com.matchme.connection;

import com.matchme.connection.dto.ConnectionRequestResponse;
import com.matchme.connection.dto.ConnectionsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ConnectionController {

    private final ConnectionRepository connectionRepository;
    private final ConnectionRequestRepository connectionRequestRepository;

    public ConnectionController(ConnectionRepository connectionRepository,
                                ConnectionRequestRepository connectionRequestRepository) {
        this.connectionRepository = connectionRepository;
        this.connectionRequestRepository = connectionRequestRepository;
    }

    // Phase 6: Send a connection request
    @PostMapping("/connections/request")
    public ResponseEntity<ConnectionRequestResponse> sendConnectionRequest(Authentication authentication,
                                                                            @RequestParam Long receiverId) {
        Long senderId = (Long) authentication.getPrincipal();

        // Prevent sending request to yourself
        if (senderId.equals(receiverId)) {
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

        ConnectionRequest request = new ConnectionRequest();
        request.setSenderId(senderId);
        request.setReceiverId(receiverId);
        ConnectionRequest saved = connectionRequestRepository.save(request);

        return ResponseEntity.ok(new ConnectionRequestResponse(saved.getId()));
    }

    // Phase 7: View incoming requests
    @GetMapping("/connections/requests")
    public ResponseEntity<ConnectionsResponse> getIncomingRequests(Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();

        List<Long> requesterIds = connectionRequestRepository.findIncomingRequests(currentUserId)
                .stream()
                .sorted(Comparator.comparing(ConnectionRequest::getCreatedAt).reversed())
                .map(ConnectionRequest::getSenderId)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ConnectionsResponse(requesterIds));
    }

    // Phase 8: Accept a connection request
    @PostMapping("/connections/accept")
    public ResponseEntity<Void> acceptConnectionRequest(Authentication authentication,
                                                         @RequestParam Long requestId) {
        Long currentUserId = (Long) authentication.getPrincipal();

        ConnectionRequest request = connectionRequestRepository.findById(requestId).orElse(null);
        if (request == null) {
            return ResponseEntity.notFound().build();
        }

        // Only the receiver can accept
        if (!request.getReceiverId().equals(currentUserId)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        // Create the connection (ensure user1Id < user2Id for consistency)
        Connection connection = new Connection();
        if (request.getSenderId() < request.getReceiverId()) {
            connection.setUser1Id(request.getSenderId());
            connection.setUser2Id(request.getReceiverId());
        } else {
            connection.setUser1Id(request.getReceiverId());
            connection.setUser2Id(request.getSenderId());
        }
        connectionRepository.save(connection);

        // Delete the request
        connectionRequestRepository.delete(request);

        return ResponseEntity.ok().build();
    }

    // Phase 8: Dismiss a connection request
    @PostMapping("/connections/dismiss")
    public ResponseEntity<Void> dismissConnectionRequest(Authentication authentication,
                                                          @RequestParam Long requestId) {
        Long currentUserId = (Long) authentication.getPrincipal();

        ConnectionRequest request = connectionRequestRepository.findById(requestId).orElse(null);
        if (request == null) {
            return ResponseEntity.notFound().build();
        }

        // Only the receiver can dismiss
        if (!request.getReceiverId().equals(currentUserId)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        // Delete the request
        connectionRequestRepository.delete(request);

        return ResponseEntity.ok().build();
    }

    // Phase 9: Get connected users
    @GetMapping("/connections")
    public ResponseEntity<ConnectionsResponse> getConnections(Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();

        List<Connection> connections = connectionRepository.findByUserId(currentUserId);

        // Extract the other user's ID from each connection
        List<Long> connectedUserIds = connections.stream()
                .map(connection -> {
                    if (connection.getUser1Id().equals(currentUserId)) {
                        return connection.getUser2Id();
                    } else {
                        return connection.getUser1Id();
                    }
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ConnectionsResponse(connectedUserIds));
    }

    // Phase 10: Disconnect from a user
    @DeleteMapping("/connections/{connectionId}")
    public ResponseEntity<Void> disconnect(Authentication authentication,
                                            @PathVariable Long connectionId) {
        Long currentUserId = (Long) authentication.getPrincipal();

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
