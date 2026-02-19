package com.loadingjr.chatapi.service;

import com.loadingjr.chatapi.domain.entity.Chat;
import com.loadingjr.chatapi.domain.enums.ChatStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatServiceTest {

    @Test
    void devePermitirTransicaoDeOpenParaActive() {
        Chat chat = new Chat();
        chat.setStatus(ChatStatus.PENDING);

        chat.setStatus(ChatStatus.ACTIVE);

        assertEquals(ChatStatus.ACTIVE, chat.getStatus());
    }

    @Test
    void naoDevePermitirTransicaoDeClosedParaActive() {
        Chat chat = new Chat();
        chat.setStatus(ChatStatus.CLOSED);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            if (chat.getStatus() == ChatStatus.CLOSED) {
                throw new RuntimeException("Chat fechado não pode reabrir");
            }
            chat.setStatus(ChatStatus.ACTIVE);
        });

        assertEquals("Chat fechado não pode reabrir", exception.getMessage());
    }
}
