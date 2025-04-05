package com.learning.realtimechatappbackend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String senderId;
    private String receiverId;
    private String conversationId;
    private String content;
    private String type; // TEXT, IMAGE, FILE,...
    private LocalDateTime sentAt;
}

