package com.matchme.chat.dto;

import java.time.Instant;

// A single chat row in the chat list
public class ChatListItem {
    public Long chatId;
    public Long otherUserId;
    public String lastMessage;
    public Instant lastMessageAt;
    public long unreadCount;

    public ChatListItem(Long chatId, Long otherUserId, String lastMessage,
                        Instant lastMessageAt, long unreadCount) {
        this.chatId = chatId;
        this.otherUserId = otherUserId;
        this.lastMessage = lastMessage;
        this.lastMessageAt = lastMessageAt;
        this.unreadCount = unreadCount;
    }
}
