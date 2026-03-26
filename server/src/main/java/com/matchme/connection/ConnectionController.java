package com.matchme.connection;

import com.matchme.connection.dto.ConnectionsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ConnectionController {

    private final ConnectionRepository connectionRepository;

    public ConnectionController(ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
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
}
