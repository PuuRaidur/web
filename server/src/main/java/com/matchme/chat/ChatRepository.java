package com.matchme.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // find a chat for a user pair (either order)
    Optional<Chat> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);
    Optional<Chat> findByUser2IdAndUser1Id(Long user2Id, Long user1Id);

    // list chats where the user is either participant
    @Query("SELECT c FROM Chat c WHERE c.user1Id = :userId OR c.user2Id = :userId")
    List<Chat> findByUserId(@Param("userId") Long userId);
}
