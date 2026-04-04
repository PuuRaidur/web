package com.matchme.connection;

import com.matchme.connection.dto.ConnectionRequestAction;
import com.matchme.connection.dto.ConnectionRequestCreate;
import com.matchme.connection.dto.ConnectionRequestsResponse;
import com.matchme.connection.dto.ConnectionsResponse;
import com.matchme.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ConnectionController {

    private final ConnectionRepository connectionRepository;
    private final ConnectionRequestRepository connectionRequestRepository;
    private final UserRepository userRepository;

    public ConnectionController(ConnectionRepository connectionRepository,
                                ConnectionRequestRepository connectionRequestRepository,
                                UserRepository userRepository) {
        this.connectionRepository = connectionRepository;
        this.connectionRequestRepository = connectionRequestRepository;
        this.userRepository = userRepository;
    }

    // Get list of connected user IDs for the current user
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

    // Get list of incoming connection request IDs for the current user
    @GetMapping("/connections/requests")
    public ResponseEntity<ConnectionRequestsResponse> getConnectionRequests(Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();

        List<Long> senderIds = connectionRequestRepository.findByReceiverId(currentUserId)
                .stream()
                .map(ConnectionRequest::getSenderId)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ConnectionRequestsResponse(senderIds));
    }

    // Get list of outgoing connection request IDs for the current user
    @GetMapping("/connections/requests/outgoing")
    public ResponseEntity<ConnectionRequestsResponse> getOutgoingConnectionRequests(Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();

        List<Long> receiverIds = connectionRequestRepository.findBySenderId(currentUserId)
                .stream()
                .map(ConnectionRequest::getReceiverId)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ConnectionRequestsResponse(receiverIds));
    }

    // Send a connection request to another user.
    @PostMapping("/connections/request")
    public ResponseEntity<Void> sendConnectionRequest(Authentication authentication,
                                                      @RequestBody ConnectionRequestCreate request) {
        Long currentUserId = (Long) authentication.getPrincipal();

        if (request == null || request.receiverId == null) {
            return ResponseEntity.badRequest().build();
        }

        if (currentUserId.equals(request.receiverId)) {
            return ResponseEntity.badRequest().build();
        }

        // Ensure receiver exists.
        if (userRepository.findById(request.receiverId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Skip if users are already connected.
        if (connectionRepository.existsBetweenUsers(currentUserId, request.receiverId)) {
            return ResponseEntity.ok().build();
        }

        // Skip if request already exists.
        if (connectionRequestRepository.existsBySenderIdAndReceiverId(currentUserId, request.receiverId)) {
            return ResponseEntity.ok().build();
        }

        ConnectionRequest connectionRequest = new ConnectionRequest();
        connectionRequest.setSenderId(currentUserId);
        connectionRequest.setReceiverId(request.receiverId);
        connectionRequest.setCreatedAt(Instant.now());
        connectionRequestRepository.save(connectionRequest);

        return ResponseEntity.ok().build();
    }

    // Accept a connection request.
    @PostMapping("/connections/accept")
    @Transactional
    public ResponseEntity<Void> acceptConnectionRequest(Authentication authentication,
                                                        @RequestBody ConnectionRequestAction request) {
        Long currentUserId = (Long) authentication.getPrincipal();

        if (request == null || request.senderId == null) {
            return ResponseEntity.badRequest().build();
        }

        ConnectionRequest connectionRequest = connectionRequestRepository
                .findBySenderIdAndReceiverId(request.senderId, currentUserId)
                .orElse(null);

        if (connectionRequest == null) {
            return ResponseEntity.notFound().build();
        }

        // Create connection if it does not exist yet.
        if (!connectionRepository.existsBetweenUsers(request.senderId, currentUserId)) {
            Connection connection = new Connection();
            Long user1 = Math.min(request.senderId, currentUserId);
            Long user2 = Math.max(request.senderId, currentUserId);
            connection.setUser1Id(user1);
            connection.setUser2Id(user2);
            connection.setCreatedAt(Instant.now());
            connectionRepository.save(connection);
        }

        // Remove the request after accepting.
        connectionRequestRepository.deleteBySenderIdAndReceiverId(request.senderId, currentUserId);

        return ResponseEntity.ok().build();
    }

    // Dismiss a connection request.
    @PostMapping("/connections/dismiss")
    @Transactional
    public ResponseEntity<Void> dismissConnectionRequest(Authentication authentication,
                                                         @RequestBody ConnectionRequestAction request) {
        Long currentUserId = (Long) authentication.getPrincipal();

        if (request == null || request.senderId == null) {
            return ResponseEntity.badRequest().build();
        }

        connectionRequestRepository.deleteBySenderIdAndReceiverId(request.senderId, currentUserId);
        return ResponseEntity.ok().build();
    }
}
