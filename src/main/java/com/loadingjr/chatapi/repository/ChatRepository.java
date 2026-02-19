package com.loadingjr.chatapi.repository;

import com.loadingjr.chatapi.domain.entity.Chat;
import java.util.List;
import com.loadingjr.chatapi.domain.entity.User;
import com.loadingjr.chatapi.domain.enums.ChatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("""
        SELECT c FROM Chat c
        WHERE c.status = :status
        AND (c.user1 = :user OR c.user2 = :user)
    """)
    Optional<Chat> findActiveChatByUser(ChatStatus status, User user);
    
    List<Chat> findByUser1IdOrUser2Id(Long user1Id, Long user2Id);
}
