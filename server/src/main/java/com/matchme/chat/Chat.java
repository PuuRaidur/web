package com.matchme.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "chats")
@Getter
@Setter
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // one participant
    @Column(name = "user1_id", nullable = false)
    private Long user1Id;

    // other participant
    @Column(name = "user2_id", nullable = false)
    private Long user2Id;

    // when chat was created
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
