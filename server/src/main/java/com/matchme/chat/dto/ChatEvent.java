package com.matchme.chat.dto;

public class ChatEvent {
    public String type;
    public Long chatId;
    public MessageResponse message;

    public ChatEvent(String type, Long chatId, MessageResponse message) {
        this.type = type;
        this.chatId = chatId;
        this.message = message;
    }
}
