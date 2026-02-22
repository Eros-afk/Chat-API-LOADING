package com.loadingjr.chatapi.service;

import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;

import com.loadingjr.chatapi.domain.dto.ChatResponseDTO;
import com.loadingjr.chatapi.domain.dto.CreateChatDTO;
import com.loadingjr.chatapi.domain.dto.RespondChatDTO;
import com.loadingjr.chatapi.domain.entity.Chat;
import com.loadingjr.chatapi.domain.entity.User;
import com.loadingjr.chatapi.domain.enums.ChatStatus;
import com.loadingjr.chatapi.exception.BusinessRuleException;
import com.loadingjr.chatapi.exception.NotFoundException;
import com.loadingjr.chatapi.repository.ChatRepository;
import com.loadingjr.chatapi.repository.UserRepository;


@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public ChatService(ChatRepository chatRepository,
                       UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public Chat createChat(CreateChatDTO dto) {

        if (dto.requesterId().equals(dto.receiverId())) {
            throw new BusinessRuleException("Não pode criar chat consigo mesmo");
        }

        User requester = userRepository.findById(dto.requesterId())
                .orElseThrow(() -> new NotFoundException("Solicitante não encontrado"));

        User receiver = userRepository.findById(dto.receiverId())
                .orElseThrow(() -> new NotFoundException("Destinatário não encontrado"));

        Chat chat = new Chat();
        chat.setUser1(requester);
        chat.setUser2(receiver);
        chat.setStatus(ChatStatus.PENDING);

        return chatRepository.save(chat);
    }
    
    public Chat respondToChat(RespondChatDTO dto) {

        Chat chat = chatRepository.findById(dto.chatId())
                .orElseThrow(() -> new NotFoundException("Chat não encontrado"));

        if (chat.getStatus() != ChatStatus.PENDING) {
            throw new BusinessRuleException("Chat não está pendente");
        }

        if (dto.accept()) {

            chatRepository.findActiveChatByUser(ChatStatus.ACTIVE, chat.getUser1())
                    .ifPresent(c -> {
                        throw new BusinessRuleException("Usuário 1 já possui chat ativo");
                    });

            chatRepository.findActiveChatByUser(ChatStatus.ACTIVE, chat.getUser2())
                    .ifPresent(c -> {
                        throw new BusinessRuleException("Usuário 2 já possui chat ativo");
                    });

            chat.setStatus(ChatStatus.ACTIVE);

        } else {
            chat.setStatus(ChatStatus.CLOSED);
        }

        return chatRepository.save(chat);
    }
    
    public Chat closeChat(Long chatId) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException("Chat não encontrado"));

        if (chat.getStatus() != ChatStatus.ACTIVE) {
            throw new BusinessRuleException("Somente chats ativos podem ser encerrados");
        }

        chat.setStatus(ChatStatus.CLOSED);
        chat.setClosedAt(java.time.LocalDateTime.now());

        return chatRepository.save(chat);
    }
    
    public List<ChatResponseDTO> getChatsByUser(Long userId) {

        List<Chat> chats = chatRepository
                .findByUser1IdOrUser2Id(userId, userId);

        return chats.stream()
                .map(chat -> new ChatResponseDTO(
                        chat.getId(),
                        chat.getUser1().getUsername(),
                        chat.getUser2().getUsername(),
                        chat.getStatus().name(),
                        chat.getCreatedAt()
                ))
                .toList();
    }

    public List<Chat> getMyChats() {

        Long authenticatedUserId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return chatRepository.findByUser1IdOrUser2Id(
                authenticatedUserId,
                authenticatedUserId
        );
    }

}
