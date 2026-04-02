package com.matchme.chat.dto;

// outgoing payload for chat creation

public class ChatResponse {

    public Long chatId;
    public Long user1Id;
    public Long user2Id;

    public ChatResponse(Long chatId, Long user1Id, Long user2Id) {
        this.chatId = chatId;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }
}
