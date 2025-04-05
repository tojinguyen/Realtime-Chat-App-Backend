package com.learning.realtimechatappbackend.chat.service;

import com.learning.realtimechatappbackend.chat.dto.ChatMessage;
import com.learning.realtimechatappbackend.chat.enums.MessageType;
import com.learning.realtimechatappbackend.chat.model.Message;
import com.learning.realtimechatappbackend.chat.repository.MessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void save(ChatMessage dto) {
        Message message = Message.builder()
                .senderId(dto.getSenderId())
                .receiverId(dto.getReceiverId())
                .conversationId(dto.getConversationId())
                .type(MessageType.valueOf(dto.getType()))
                .content(dto.getContent())
                .sentAt(dto.getSentAt())
                .build();

        messageRepository.save(message);
    }

    public List<Message> getMessages(String conversationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        return messageRepository.findByConversationIdOrderBySentAtDesc(conversationId, pageable);
    }
}

