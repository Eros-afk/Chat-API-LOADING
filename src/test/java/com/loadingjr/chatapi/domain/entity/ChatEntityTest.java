package com.loadingjr.chatapi.domain.entity;

import com.loadingjr.chatapi.domain.enums.ChatStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ChatEntityTest {

    @Test
    void deveDefinirStatusPadraoEPrencherCreatedAtNoPrePersist() {
        Chat chat = new Chat();

        chat.prePersist();

        assertEquals(ChatStatus.PENDING, chat.getStatus());
        assertNotNull(chat.getCreatedAt());
    }
}
