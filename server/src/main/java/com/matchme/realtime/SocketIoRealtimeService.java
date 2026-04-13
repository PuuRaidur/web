package com.matchme.realtime;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.matchme.auth.JwtService;
import com.matchme.chat.ChatRepository;
import com.matchme.chat.dto.ChatEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SocketIoRealtimeService {
    private static final Logger log = LoggerFactory.getLogger(SocketIoRealtimeService.class);

    private final JwtService jwtService;
    private final ChatRepository chatRepository;
    private final String host;
    private final int port;

    private SocketIOServer socketServer;
    private final ConcurrentHashMap<Long, Set<UUID>> userSessions = new ConcurrentHashMap<>();

    public SocketIoRealtimeService(JwtService jwtService,
                                   ChatRepository chatRepository,
                                   @Value("${socketio.host}") String host,
                                   @Value("${socketio.port}") int port) {
        this.jwtService = jwtService;
        this.chatRepository = chatRepository;
        this.host = host;
        this.port = port;
    }

    @PostConstruct
    public void start() {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);
        config.setOrigin("*");

        socketServer = new SocketIOServer(config);

        socketServer.addConnectListener(client -> {
            Long userId = authenticateClient(client);
            if (userId == null) {
                client.disconnect();
                return;
            }

            client.set("userId", userId);
            userSessions.compute(userId, (id, sessions) -> {
                Set<UUID> next = sessions == null ? ConcurrentHashMap.newKeySet() : sessions;
                next.add(client.getSessionId());
                return next;
            });

            broadcastPresence(userId, true);
            log.info("Socket connected for userId={} sessionId={}", userId, client.getSessionId());
        });

        socketServer.addDisconnectListener(client -> {
            Long userId = client.get("userId");
            if (userId == null) {
                return;
            }

            userSessions.computeIfPresent(userId, (id, sessions) -> {
                sessions.remove(client.getSessionId());
                return sessions.isEmpty() ? null : sessions;
            });

            if (!userSessions.containsKey(userId)) {
                broadcastPresence(userId, false);
            }
            log.info("Socket disconnected for userId={} sessionId={}", userId, client.getSessionId());
        });

        socketServer.addEventListener("typing", TypingPayload.class, (client, data, ackSender) -> {
            Long senderId = client.get("userId");
            if (senderId == null || data == null || data.chatId == null || data.isTyping == null) {
                return;
            }

            var chat = chatRepository.findById(data.chatId).orElse(null);
            if (chat == null) {
                return;
            }

            boolean isParticipant = chat.getUser1Id().equals(senderId) || chat.getUser2Id().equals(senderId);
            if (!isParticipant) {
                return;
            }

            Long otherUserId = chat.getUser1Id().equals(senderId) ? chat.getUser2Id() : chat.getUser1Id();
            emitChatEventToUser(otherUserId, new ChatEvent("typing", data.chatId, senderId, data.isTyping, null));
        });

        socketServer.start();
    }

    @PreDestroy
    public void stop() {
        if (socketServer != null) {
            socketServer.stop();
        }
    }

    public void emitChatEventToUser(Long userId, ChatEvent event) {
        Set<UUID> sessions = userSessions.get(userId);
        if (sessions == null || sessions.isEmpty()) {
            log.info("No active socket sessions for userId={}, eventType={}, chatId={}",
                    userId, event.type, event.chatId);
            return;
        }
        Map<String, Object> payload = toSocketPayload(event);
        sessions.forEach(sessionId -> {
            SocketIOClient client = socketServer.getClient(sessionId);
            if (client != null) {
                client.sendEvent("chat-update", payload);
                log.info("Emitted chat-update to userId={} sessionId={} eventType={} chatId={}",
                        userId, sessionId, event.type, event.chatId);
            }
        });
    }

    public Set<Long> getOnlineUserIds() {
        return userSessions.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private void broadcastPresence(Long userId, boolean online) {
        socketServer.getBroadcastOperations().sendEvent("presence", new PresenceEvent(userId, online));
    }

    private Long authenticateClient(SocketIOClient client) {
        String token = client.getHandshakeData().getSingleUrlParam("token");
        if (token == null || token.isBlank()) {
            return null;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            return jwtService.parseUserId(token);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Map<String, Object> toSocketPayload(ChatEvent event) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", event.type);
        payload.put("chatId", event.chatId);
        payload.put("senderId", event.senderId);
        payload.put("isTyping", event.isTyping);

        if (event.message != null) {
            Map<String, Object> message = new HashMap<>();
            message.put("id", event.message.id);
            message.put("chatId", event.message.chatId);
            message.put("senderId", event.message.senderId);
            message.put("content", event.message.content);
            message.put("createdAt", event.message.createdAt != null ? event.message.createdAt.toString() : null);
            payload.put("message", message);
        } else {
            payload.put("message", null);
        }

        return payload;
    }

    public static class TypingPayload {
        public Long chatId;
        public Boolean isTyping;
    }
}
