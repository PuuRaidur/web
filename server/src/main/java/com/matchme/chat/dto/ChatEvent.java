package com.matchme.chat.dto;

public class ChatEvent {
    public String type;
    public Long chatId;
    public Long senderId;
    public Boolean isTyping;
    public MessageResponse message;

    public ChatEvent(String type, Long chatId, Long senderId, Boolean isTyping, MessageResponse message) {
        this.type = type;
        this.chatId = chatId;
        this.senderId = senderId;
        this.isTyping = isTyping;
        this.message = message;
    }
}
