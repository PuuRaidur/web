package com.matchme.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChatReadRepository extends JpaRepository<ChatRead, Long> {
    // Find read state for a specific user + chat
    Optional<ChatRead> findByChatIdAndUserId(Long chatId, Long userId);
}
