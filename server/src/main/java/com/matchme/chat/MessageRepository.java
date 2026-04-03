package com.matchme.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;


public interface MessageRepository extends JpaRepository<Message, Long> {

    // paginated message history for chat
    Page<Message> findByChatIdOrderByCreatedAtDesc(Long chatId, Pageable pageable);

    // Last message in a chat
    Optional<Message> findFirstByChatIdOrderByCreatedAtDesc(Long chatId);

    // Count unread messages after last_read_at
    long countByChatIdAndCreatedAtAfter(Long chatId, Instant lastReadAt);

}
