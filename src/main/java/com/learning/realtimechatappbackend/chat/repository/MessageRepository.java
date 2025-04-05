package com.learning.realtimechatappbackend.chat.repository;

import com.learning.realtimechatappbackend.chat.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByConversationIdOrderBySentAtDesc(String conversationId, Pageable pageable);
}
