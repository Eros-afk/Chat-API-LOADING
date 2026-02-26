package com.loadingjr.chatapi.service;

import com.loadingjr.chatapi.domain.dto.RespondChatDTO;
import com.loadingjr.chatapi.domain.entity.Chat;
import com.loadingjr.chatapi.domain.entity.User;
import com.loadingjr.chatapi.domain.enums.ChatStatus;
import com.loadingjr.chatapi.exception.BadRequestException;
import com.loadingjr.chatapi.exception.ForbiddenException;
import com.loadingjr.chatapi.repository.ChatRepository;
import com.loadingjr.chatapi.repository.UserRepository;
import com.loadingjr.chatapi.security.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ChatService chatService;

    private Chat pendingChat;

    @BeforeEach
    void setUp() {
        User requester = new User();
        requester.setId(1L);
        requester.setUsername("joao");

        User receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("maria");

        pendingChat = new Chat();
        pendingChat.setId(10L);
        pendingChat.setUser1(requester);
        pendingChat.setUser2(receiver);
        pendingChat.setStatus(ChatStatus.PENDING);
    }

    @Test
    void devePermitirTransicaoDePendingParaActiveAoAceitar() {
        when(chatRepository.findById(10L)).thenReturn(Optional.of(pendingChat));
        when(chatRepository.findActiveChatByUser(ChatStatus.ACTIVE, pendingChat.getUser1())).thenReturn(Optional.empty());
        when(chatRepository.findActiveChatByUser(ChatStatus.ACTIVE, pendingChat.getUser2())).thenReturn(Optional.empty());
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(authService.getAuthenticatedUserId()).thenReturn(2L);

        Chat chat = chatService.respondToChat(new RespondChatDTO(10L, true));

        assertEquals(ChatStatus.ACTIVE, chat.getStatus());
    }

    @Test
    void naoDevePermitirResponderChatFechado() {
        pendingChat.setStatus(ChatStatus.CLOSED);
        when(chatRepository.findById(10L)).thenReturn(Optional.of(pendingChat));

        assertThrows(BadRequestException.class,
                () -> chatService.respondToChat(new RespondChatDTO(10L, true)));
    }

    @Test
    void naoDevePermitirRespostaPorQuemNaoRecebeuSolicitacao() {
        when(chatRepository.findById(10L)).thenReturn(Optional.of(pendingChat));
        when(authService.getAuthenticatedUserId()).thenReturn(1L);

        assertThrows(ForbiddenException.class,
                () -> chatService.respondToChat(new RespondChatDTO(10L, true)));
    }
}
