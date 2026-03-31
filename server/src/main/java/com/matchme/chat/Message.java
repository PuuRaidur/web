package com.matchme.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "messages")
@Getter
@Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // which hat this message belongs to
    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    // who sent the message
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    // message txt
    @Column(name = "content", nullable = false)
    private String content;

    // when message was snt
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
