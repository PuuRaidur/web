package com.matchme.chat;

import com.matchme.chat.dto.ChatListItem;
import com.matchme.chat.dto.ChatResponse;
import com.matchme.chat.dto.MessageResponse;
import com.matchme.chat.dto.SendMessageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.time.Instant;



@RestController
@RequestMapping("/chats")
public class ChatController {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final ChatReadRepository chatReadRepository;


    public ChatController(ChatRepository chatRepository,
                          MessageRepository messageRepository,
                          ChatReadRepository chatReadRepository) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.chatReadRepository = chatReadRepository;
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

    // List chats for current user, ordered by most recent message
    @GetMapping
    public ResponseEntity<List<ChatListItem>> listChats(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        var chats = chatRepository.findByUserId(userId);

        List<ChatListItem> items = chats.stream()
                .map(chat -> {
                    Long otherUserId = chat.getUser1Id().equals(userId)
                            ? chat.getUser2Id()
                            : chat.getUser1Id();

                    var lastMessageOpt = messageRepository.findFirstByChatIdOrderByCreatedAtDesc(chat.getId());
                    String lastMessage = lastMessageOpt.map(Message::getContent).orElse(null);
                    var lastMessageAt = lastMessageOpt.map(Message::getCreatedAt).orElse(null);

                    // Determine unread count using chat_reads
                    var read = chatReadRepository.findByChatIdAndUserId(chat.getId(), userId).orElse(null);
                    long unreadCount = 0;
                    if (read != null) {
                        unreadCount = messageRepository.countByChatIdAndCreatedAtAfter(
                                chat.getId(),
                                read.getLastReadAt()
                        );
                    } else {
                        // If no read record, count all messages
                        unreadCount = messageRepository.countByChatIdAndCreatedAtAfter(
                                chat.getId(),
                                java.time.Instant.EPOCH
                        );
                    }

                    return new ChatListItem(
                            chat.getId(),
                            otherUserId,
                            lastMessage,
                            lastMessageAt,
                            unreadCount
                    );
                })
                // Sort by most recent message time (nulls last)
                .sorted(Comparator.comparing(
                        (ChatListItem item) -> item.lastMessageAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();

        return ResponseEntity.ok(items);
    }

    // Mark a chat as read for the current user
    @PostMapping("/{chatId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long chatId,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        // Ensure chat exists
        var chat = chatRepository.findById(chatId).orElse(null);
        if (chat == null) {
            return ResponseEntity.notFound().build();
        }

        // Ensure user is a participant
        boolean isParticipant = chat.getUser1Id().equals(userId) || chat.getUser2Id().equals(userId);
        if (!isParticipant) {
            return ResponseEntity.status(403).build();
        }

        var read = chatReadRepository.findByChatIdAndUserId(chatId, userId)
                .orElseGet(() -> {
                    ChatRead cr = new ChatRead();
                    cr.setChatId(chatId);
                    cr.setUserId(userId);
                    return cr;
                });

        read.setLastReadAt(Instant.now());
        chatReadRepository.save(read);

        return ResponseEntity.ok().build();
    }




}
