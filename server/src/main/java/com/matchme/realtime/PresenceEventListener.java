package com.matchme.realtime;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class PresenceEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ConcurrentHashMap<String, Long> sessionUsers = new ConcurrentHashMap<>();

    public PresenceEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        if (principal == null) {
            return;
        }
        Long userId = Long.valueOf(principal.getName());
        sessionUsers.put(accessor.getSessionId(), userId);
        messagingTemplate.convertAndSend("/topic/presence", new PresenceEvent(userId, true));
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        Long userId = sessionUsers.remove(event.getSessionId());
        if (userId != null) {
            messagingTemplate.convertAndSend("/topic/presence", new PresenceEvent(userId, false));
        }
    }

    public Set<Long> getOnlineUserIds() {
        return sessionUsers.values().stream().collect(Collectors.toSet());
    }
}
