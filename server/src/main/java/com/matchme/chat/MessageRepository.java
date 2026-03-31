package com.matchme.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // paginated message history for chat
    Page<Message> findByChatIdOrderByCreatedAtDesc(Long chatId, Pageable pageable);
}
