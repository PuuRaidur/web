package com.matchme.chat;

import com.matchme.chat.dto.MessageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.matchme.chat.dto.ChatResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.matchme.chat.dto.SendMessageRequest;
import java.time.Instant;


import java.util.List;
@RestController
@RequestMapping("/chats")
public class ChatController {

    private MessageRepository messageRepository;
    private final ChatRepository chatRepository;

    public ChatController(MessageRepository messageRepository, ChatRepository chatRepository) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
    }

    // get paginated message history for a chat
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
            ) {
        Long userId = (Long) authentication.getPrincipal();

        // ensure the user is a participant in chat
        var chat = chatRepository.findById(chatId).orElse(null);
        if (chat == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isParticipant = chat.getUser1Id().equals(userId) || chat.getUser2Id().equals(userId);
        if (!isParticipant) {
            return ResponseEntity.status(403).build();
        }

        var pageRequest = PageRequest.of(page, size);
        var messagesPage = messageRepository.findByChatIdOrderByCreatedAtDesc(chatId, pageRequest);

        List<MessageResponse> result = messagesPage.stream()
                .map(m -> new MessageResponse(
                        m.getId(),
                        m.getChatId(),
                        m.getSenderId(),
                        m.getContent(),
                        m.getCreatedAt()
                ))
                .toList();
        return ResponseEntity.ok(result);
    }

    // Create or get a chat between current user and another user
    @PostMapping("/with")
    public ResponseEntity<ChatResponse> createOrGetChat(
            Authentication authentication,
            @RequestParam Long otherUserId
    ) {
        Long currentUserId = (Long) authentication.getPrincipal();

        // Ensure we always store user1 < user2 to enforce uniqueness
        Long user1 = Math.min(currentUserId, otherUserId);
        Long user2 = Math.max(currentUserId, otherUserId);

        // Try to find existing chat
        var existing = chatRepository.findByUser1IdAndUser2Id(user1, user2)
                .or(() -> chatRepository.findByUser2IdAndUser1Id(user1, user2));

        if (existing.isPresent()) {
            var chat = existing.get();
            return ResponseEntity.ok(new ChatResponse(chat.getId(), chat.getUser1Id(), chat.getUser2Id()));
        }

        // Create new chat
        Chat chat = new Chat();
        chat.setUser1Id(user1);
        chat.setUser2Id(user2);

        Chat saved = chatRepository.save(chat);
        return ResponseEntity.ok(new ChatResponse(saved.getId(), saved.getUser1Id(), saved.getUser2Id()));
    }

    // Send a message in a chat (only if the user is a participant)
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable Long chatId,
            Authentication authentication,
            @RequestBody SendMessageRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        // Ensure chat exists
        var chat = chatRepository.findById(chatId).orElse(null);
        if (chat == null) {
            return ResponseEntity.notFound().build();
        }

        // Ensure sender is in the chat
        boolean isParticipant = chat.getUser1Id().equals(userId) || chat.getUser2Id().equals(userId);
        if (!isParticipant) {
            return ResponseEntity.status(403).build();
        }

        // Create and save message
        Message message = new Message();
        message.setChatId(chatId);
        message.setSenderId(userId);
        message.setContent(request.content);
        message.setCreatedAt(Instant.now());

        Message saved = messageRepository.save(message);

        return ResponseEntity.ok(new MessageResponse(
                saved.getId(),
                saved.getChatId(),
                saved.getSenderId(),
                saved.getContent(),
                saved.getCreatedAt()
        ));
    }


}
