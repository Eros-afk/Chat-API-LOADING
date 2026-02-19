package com.loadingjr.chatapi.repository;

import com.loadingjr.chatapi.domain.entity.Message;
import java.util.List;
import com.loadingjr.chatapi.domain.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChat(Chat chat);
    
    List<Message> findByChatIdOrderByCreatedAtAsc(Long chatId);

    Page<Message> findByChatId(Long chatId, Pageable pageable);

}