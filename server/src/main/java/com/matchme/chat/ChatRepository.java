package com.matchme.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // find a chat for a user pair (either order)
    Optional<Chat> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);
    Optional<Chat> findByUser2IdAndUser1Id(Long user2Id, Long user1Id);
}
