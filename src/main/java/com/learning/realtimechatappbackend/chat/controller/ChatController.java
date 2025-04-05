package com.learning.realtimechatappbackend.chat.controller;

import com.learning.realtimechatappbackend.chat.dto.ChatMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

    public ChatController(KafkaTemplate<String, ChatMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @MessageMapping("/chat.send") // client gửi đến /app/chat.send
    public void sendMessage(@Payload ChatMessage message) {
        message.setSentAt(LocalDateTime.now());
        kafkaTemplate.send("chat-messages", message.getConversationId(), message);
    }
}
