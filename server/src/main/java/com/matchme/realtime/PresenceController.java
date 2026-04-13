package com.matchme.realtime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class PresenceController {

    private final PresenceEventListener presenceEventListener;

    public PresenceController(PresenceEventListener presenceEventListener) {
        this.presenceEventListener = presenceEventListener;
    }

    @GetMapping("/presence/online")
    public ResponseEntity<Set<Long>> getOnlineUsers(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(presenceEventListener.getOnlineUserIds());
    }
}
