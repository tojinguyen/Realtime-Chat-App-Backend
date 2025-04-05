package com.learning.realtimechatappbackend.chat.kafka;

import com.learning.realtimechatappbackend.chat.dto.ChatMessage;
import com.learning.realtimechatappbackend.chat.service.MessageService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    public ChatMessageConsumer(SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void consume(ChatMessage message) {
        messageService.save(message); // Lưu vào DB

        // Broadcast tới các client đang trong conversation
        messagingTemplate.convertAndSend(
                "/topic/conversations/" + message.getConversationId(),
                message
        );
    }
}

