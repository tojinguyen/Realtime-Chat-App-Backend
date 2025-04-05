package com.learning.realtimechatappbackend.chat.model;

import com.learning.realtimechatappbackend.chat.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String senderId;
    private String receiverId;
    private String conversationId;

    @Enumerated(EnumType.STRING)
    private MessageType type; // TEXT, IMAGE, FILE,...

    private String content;

    private LocalDateTime sentAt;
}

