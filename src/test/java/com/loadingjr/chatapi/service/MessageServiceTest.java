package com.loadingjr.chatapi.service;

import com.loadingjr.chatapi.domain.dto.MessageResponseDTO;
import com.loadingjr.chatapi.domain.dto.SendMessageDTO;
import com.loadingjr.chatapi.domain.entity.Chat;
import com.loadingjr.chatapi.domain.entity.Message;
import com.loadingjr.chatapi.domain.entity.User;
import com.loadingjr.chatapi.domain.enums.ChatStatus;
import com.loadingjr.chatapi.repository.ChatRepository;
import com.loadingjr.chatapi.repository.MessageRepository;
import com.loadingjr.chatapi.repository.UserRepository;
import com.loadingjr.chatapi.security.AuthenticatedUserProvider;
import com.loadingjr.chatapi.util.CryptoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CryptoService cryptoService;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @InjectMocks
    private MessageService messageService;

    private User user1;
    private User user2;
    private Chat activeChat;

    @BeforeEach
    void setUp() {
        when(authenticatedUserProvider.getAuthenticatedUserId()).thenReturn(1L);

        user1 = new User();
        user1.setId(1L);
        user1.setUsername("alice");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("bob");

        activeChat = new Chat();
        activeChat.setId(100L);
        activeChat.setUser1(user1);
        activeChat.setUser2(user2);
        activeChat.setStatus(ChatStatus.ACTIVE);
    }

    @Test
    void deveNegarListagemDeMensagensParaUsuarioForaDoChat() {
        when(authenticatedUserProvider.getAuthenticatedUserId()).thenReturn(99L);
        when(chatRepository.findById(100L)).thenReturn(Optional.of(activeChat));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> messageService.getMessagesByChat(100L));

        assertEquals("Você não pode acessar este chat", exception.getMessage());
        verify(messageRepository, never()).findByChatIdOrderByCreatedAtAsc(any());
    }

    @Test
    void deveCriptografarMensagemAoEnviar() {
        SendMessageDTO dto = new SendMessageDTO(100L, "mensagem clara");

        when(chatRepository.findById(100L)).thenReturn(Optional.of(activeChat));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(cryptoService.encrypt("mensagem clara")).thenReturn("mensagem-criptografada");
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Message saved = messageService.sendMessage(dto);

        assertEquals("mensagem-criptografada", saved.getContent());
        verify(cryptoService).encrypt("mensagem clara");

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(captor.capture());
        assertEquals("mensagem-criptografada", captor.getValue().getContent());
        assertEquals(user1, captor.getValue().getSender());
        assertEquals(activeChat, captor.getValue().getChat());
    }

    @Test
    void deveDescriptografarMensagensAoListar() {
        Message encryptedMessage = new Message();
        encryptedMessage.setId(321L);
        encryptedMessage.setChat(activeChat);
        encryptedMessage.setSender(user1);
        encryptedMessage.setContent("cipher-text");
        encryptedMessage.setCreatedAt(LocalDateTime.now());

        when(chatRepository.findById(100L)).thenReturn(Optional.of(activeChat));
        when(messageRepository.findByChatIdOrderByCreatedAtAsc(100L)).thenReturn(List.of(encryptedMessage));
        when(cryptoService.decrypt("cipher-text")).thenReturn("plain-text");

        List<MessageResponseDTO> result = messageService.getMessagesByChat(100L);

        assertEquals(1, result.size());
        assertEquals("plain-text", result.get(0).content());
        verify(cryptoService).decrypt("cipher-text");
    }
}
