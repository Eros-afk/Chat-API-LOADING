package com.loadingjr.chatapi.service;

import com.loadingjr.chatapi.domain.dto.CreateChatDTO;
import com.loadingjr.chatapi.domain.dto.RespondChatDTO;
import com.loadingjr.chatapi.domain.entity.Chat;
import com.loadingjr.chatapi.domain.entity.User;
import com.loadingjr.chatapi.domain.enums.ChatStatus;
import com.loadingjr.chatapi.repository.ChatRepository;
import com.loadingjr.chatapi.repository.UserRepository;
import com.loadingjr.chatapi.security.AuthenticatedUserProvider;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @InjectMocks
    private ChatService chatService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("alice");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("bob");

        when(authenticatedUserProvider.getAuthenticatedUserId()).thenReturn(1L);
    }

    @Test
    void deveFalharAoCriarChatComMesmoUsuario() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chatService.createChat(new CreateChatDTO(1L)));

        assertEquals("Não pode criar chat consigo mesmo", exception.getMessage());
        verify(userRepository, never()).findById(any());
        verify(chatRepository, never()).save(any());
    }

    @Test
    void deveFalharAoResponderQuandoChatNaoExiste() {
        when(chatRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chatService.respondToChat(new RespondChatDTO(99L, true)));

        assertEquals("Chat não encontrado", exception.getMessage());
        verify(chatRepository, never()).save(any());
    }

    @Test
    void deveFalharAoAceitarChatPendenteQuandoUsuarioJaTemChatAtivo() {
        Chat pendingChat = new Chat();
        pendingChat.setId(10L);
        pendingChat.setUser1(user1);
        pendingChat.setUser2(user2);
        pendingChat.setStatus(ChatStatus.PENDING);

        Chat activeConflict = new Chat();
        activeConflict.setId(11L);
        activeConflict.setStatus(ChatStatus.ACTIVE);

        when(authenticatedUserProvider.getAuthenticatedUserId()).thenReturn(2L);
        when(chatRepository.findById(10L)).thenReturn(Optional.of(pendingChat));
        when(chatRepository.findActiveChatByUser(ChatStatus.ACTIVE, user1)).thenReturn(Optional.of(activeConflict));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chatService.respondToChat(new RespondChatDTO(10L, true)));

        assertEquals("Usuário 1 já possui chat ativo", exception.getMessage());
        verify(chatRepository, never()).save(any());
    }

    @Test
    void deveFalharAoFecharChatInexistente() {
        when(chatRepository.findById(77L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chatService.closeChat(77L));

        assertEquals("Chat não encontrado", exception.getMessage());
        verify(chatRepository, never()).save(any());
    }

    @Test
    void deveFalharAoFecharChatQuandoNaoEstaAtivo() {
        Chat pendingChat = new Chat();
        pendingChat.setId(30L);
        pendingChat.setUser1(user1);
        pendingChat.setUser2(user2);
        pendingChat.setStatus(ChatStatus.PENDING);

        when(chatRepository.findById(30L)).thenReturn(Optional.of(pendingChat));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chatService.closeChat(30L));

        assertEquals("Somente chats ativos podem ser encerrados", exception.getMessage());
        verify(chatRepository, never()).save(any());
    }

    @Test
    void deveAtivarChatQuandoDestinatarioAceitaSolicitacaoPendente() {
        Chat pendingChat = new Chat();
        pendingChat.setId(10L);
        pendingChat.setUser1(user1);
        pendingChat.setUser2(user2);
        pendingChat.setStatus(ChatStatus.PENDING);

        when(authenticatedUserProvider.getAuthenticatedUserId()).thenReturn(2L);
        when(chatRepository.findById(10L)).thenReturn(Optional.of(pendingChat));
        when(chatRepository.findActiveChatByUser(ChatStatus.ACTIVE, user1)).thenReturn(Optional.empty());
        when(chatRepository.findActiveChatByUser(ChatStatus.ACTIVE, user2)).thenReturn(Optional.empty());
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Chat result = chatService.respondToChat(new RespondChatDTO(10L, true));

        assertEquals(ChatStatus.ACTIVE, result.getStatus());
    }

    @Test
    void deveFecharChatQuandoDestinatarioRecusaSolicitacaoPendente() {
        Chat pendingChat = new Chat();
        pendingChat.setId(10L);
        pendingChat.setUser1(user1);
        pendingChat.setUser2(user2);
        pendingChat.setStatus(ChatStatus.PENDING);

        when(authenticatedUserProvider.getAuthenticatedUserId()).thenReturn(2L);
        when(chatRepository.findById(10L)).thenReturn(Optional.of(pendingChat));
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Chat result = chatService.respondToChat(new RespondChatDTO(10L, false));

        assertEquals(ChatStatus.CLOSED, result.getStatus());
    }
}
