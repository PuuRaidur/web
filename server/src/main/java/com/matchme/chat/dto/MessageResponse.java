package com.matchme.chat.dto;

import java.time.Instant;

// Outgoing message payload for chat history
public class MessageResponse {
    public Long id;
    public Long chatId;
    public Long senderId;
    public String content;
    public Instant createdAt;

    public MessageResponse(Long id, Long chatId, Long senderId, String content, Instant createdAt) {
        this.id = id;
        this.chatId = chatId;
        this.senderId = senderId;
        this.content = content;
        this.createdAt = createdAt;
    }
}
