package com.matchme.chat;

import com.matchme.chat.dto.ChatEvent;
import com.matchme.chat.dto.ChatTypingRequest;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatRealtimeController {

    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatRealtimeController(ChatRepository chatRepository,
                                  SimpMessagingTemplate messagingTemplate) {
        this.chatRepository = chatRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.typing")
    public void handleTyping(ChatTypingRequest request, Principal principal) {
        if (request == null || request.chatId == null || request.isTyping == null) {
            return;
        }
        if (principal == null) {
            return;
        }

        Long senderId = Long.valueOf(principal.getName());
        var chat = chatRepository.findById(request.chatId).orElse(null);
        if (chat == null) {
            return;
        }

        boolean isParticipant = chat.getUser1Id().equals(senderId) || chat.getUser2Id().equals(senderId);
        if (!isParticipant) {
            return;
        }

        Long otherUserId = chat.getUser1Id().equals(senderId) ? chat.getUser2Id() : chat.getUser1Id();
        ChatEvent event = new ChatEvent("typing", request.chatId, senderId, request.isTyping, null);
        messagingTemplate.convertAndSendToUser(String.valueOf(otherUserId), "/queue/chat-updates", event);
    }
}
