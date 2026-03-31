package com.matchme.chat;

import com.matchme.chat.dto.MessageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
}
