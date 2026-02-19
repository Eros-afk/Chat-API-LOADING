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
import com.loadingjr.chatapi.util.CryptoService;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final CryptoService cryptoService;

    public MessageService(MessageRepository messageRepository,
                          ChatRepository chatRepository,
                          UserRepository userRepository,
                          CryptoService cryptoService) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.cryptoService = cryptoService;
    }
    
    public List<MessageResponseDTO> getMessagesByChat(Long chatId) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat não encontrado"));

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

        Chat chat = chatRepository.findById(dto.chatId())
                .orElseThrow(() -> new RuntimeException("Chat não encontrado"));

        if (chat.getStatus() != ChatStatus.ACTIVE) {
            throw new RuntimeException("Chat não está ativo");
        }

        User sender = userRepository.findById(dto.senderId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!chat.getUser1().getId().equals(sender.getId()) &&
            !chat.getUser2().getId().equals(sender.getId())) {
            throw new RuntimeException("Usuário não pertence a este chat");
        }

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(cryptoService.encrypt(dto.content()));
        //message.setCreatedAt(LocalDateTime.now());
        
        return messageRepository.save(message);
    }
}
