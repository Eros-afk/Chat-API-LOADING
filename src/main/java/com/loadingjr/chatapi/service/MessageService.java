package com.loadingjr.chatapi.service;

import com.loadingjr.chatapi.domain.dto.MessageResponseDTO;
import com.loadingjr.chatapi.domain.dto.SendMessageDTO;
import com.loadingjr.chatapi.domain.dto.UserResponseDTO;
import com.loadingjr.chatapi.domain.entity.Chat;
import com.loadingjr.chatapi.domain.entity.Message;
import com.loadingjr.chatapi.domain.entity.User;
import com.loadingjr.chatapi.domain.enums.ChatStatus;
import com.loadingjr.chatapi.exception.BadRequestException;
import com.loadingjr.chatapi.exception.ForbiddenException;
import com.loadingjr.chatapi.exception.NotFoundException;
import com.loadingjr.chatapi.repository.ChatRepository;
import com.loadingjr.chatapi.repository.MessageRepository;
import com.loadingjr.chatapi.repository.UserRepository;
import com.loadingjr.chatapi.security.AuthService;
import com.loadingjr.chatapi.util.CryptoService;

import org.springframework.stereotype.Service;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final CryptoService cryptoService;
    private final AuthService authService;

    public MessageService(MessageRepository messageRepository,
                          ChatRepository chatRepository,
                          UserRepository userRepository,
                          CryptoService cryptoService,
                          AuthService authService) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.cryptoService = cryptoService;
        this.authService = authService;
    }

    public List<MessageResponseDTO> getMessagesByChat(Long chatId) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException("Chat não encontrado"));

        Long authenticatedUserId = authService.getAuthenticatedUserId();

        if (!chat.getUser1().getId().equals(authenticatedUserId) &&
            !chat.getUser2().getId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Você não pode acessar este chat");
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

        Long authenticatedUserId = authService.getAuthenticatedUserId();

        if (!authenticatedUserId.equals(dto.senderId())) {
            throw new ForbiddenException("Usuário não autorizado");
        }

        Chat chat = chatRepository.findById(dto.chatId())
                .orElseThrow(() -> new NotFoundException("Chat não encontrado"));

        if (chat.getStatus() != ChatStatus.ACTIVE) {
            throw new BadRequestException("Chat não está ativo");
        }

        if (!chat.getUser1().getId().equals(authenticatedUserId) &&
            !chat.getUser2().getId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Você não participa deste chat");
        }

        User sender = userRepository.findById(dto.senderId())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        if (!chat.getUser1().getId().equals(sender.getId()) &&
            !chat.getUser2().getId().equals(sender.getId())) {
            throw new ForbiddenException("Usuário não pertence a este chat");
        }

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(cryptoService.encrypt(dto.content()));

        return messageRepository.save(message);
    }

    public Page<MessageResponseDTO> getMessages(Long chatId, Pageable pageable) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException("Chat não encontrado"));

        Long authenticatedUserId = authService.getAuthenticatedUserId();
        if (!chat.getUser1().getId().equals(authenticatedUserId) &&
                !chat.getUser2().getId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Você não pode acessar este chat");
        }

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
