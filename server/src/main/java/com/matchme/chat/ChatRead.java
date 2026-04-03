package com.matchme.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "chat_reads")
@Getter
@Setter
public class ChatRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which chat this read state belongs to
    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    // Which user this read state belongs to
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // Last time the user read this chat
    @Column(name = "last_read_at", nullable = false)
    private Instant lastReadAt = Instant.now();
}
