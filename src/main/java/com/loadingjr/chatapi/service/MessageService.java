package com.loadingjr.chatapi.service;

import com.loadingjr.chatapi.domain.dto.MessageResponseDTO;
import com.loadingjr.chatapi.domain.dto.SendMessageDTO;
import com.loadingjr.chatapi.domain.dto.UserResponseDTO;
import com.loadingjr.chatapi.domain.entity.Chat;
import com.loadingjr.chatapi.domain.entity.Message;
import com.loadingjr.chatapi.domain.entity.User;
import com.loadingjr.chatapi.domain.enums.ChatStatus;
import com.loadingjr.chatapi.repository.ChatRepository;
import com.loadingjr.chatapi.repository.MessageRepository;
import com.loadingjr.chatapi.repository.UserRepository;
import com.loadingjr.chatapi.security.AuthenticatedUserProvider;
import com.loadingjr.chatapi.util.CryptoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final CryptoService cryptoService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public MessageService(MessageRepository messageRepository,
                          ChatRepository chatRepository,
                          UserRepository userRepository,
                          CryptoService cryptoService,
                          AuthenticatedUserProvider authenticatedUserProvider) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.cryptoService = cryptoService;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public List<MessageResponseDTO> getMessagesByChat(Long chatId) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat não encontrado"));

        Long authenticatedUserId = authenticatedUserProvider.getAuthenticatedUserId();

        if (!chat.getUser1().getId().equals(authenticatedUserId) &&
            !chat.getUser2().getId().equals(authenticatedUserId)) {
            throw new RuntimeException("Você não pode acessar este chat");
        }

        return messageRepository.findByChatIdOrderByCreatedAtAsc(chat.getId())
                .stream()
                .map(message -> new MessageResponseDTO(
                        message.getId(),
                        cryptoService.decrypt(message.getContent()),
                        message.getCreatedAt(),
                        new UserResponseDTO(
                                message.getSender().getId(),
                                message.getSender().getUsername()
                        )
                ))
                .toList();
    }

    public Message sendMessage(SendMessageDTO dto) {

        Long authenticatedUserId = authenticatedUserProvider.getAuthenticatedUserId();

        Chat chat = chatRepository.findById(dto.chatId())
                .orElseThrow(() -> new RuntimeException("Chat não encontrado"));

        if (chat.getStatus() != ChatStatus.ACTIVE) {
            throw new RuntimeException("Chat não está ativo");
        }

        if (!chat.getUser1().getId().equals(authenticatedUserId) &&
            !chat.getUser2().getId().equals(authenticatedUserId)) {
            throw new RuntimeException("Você não participa deste chat");
        }

        User sender = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(cryptoService.encrypt(dto.content()));

        return messageRepository.save(message);
    }

    public Page<MessageResponseDTO> getMessages(Long chatId, Pageable pageable) {

        Page<Message> page = messageRepository.findByChatId(chatId, pageable);

        return page.map(message -> new MessageResponseDTO(
                message.getId(),
                cryptoService.decrypt(message.getContent()),
                message.getCreatedAt(),
                new UserResponseDTO(
                        message.getSender().getId(),
                        message.getSender().getUsername()
                )
        ));
    }

}
