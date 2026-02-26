package com.loadingjr.chatapi.service;

import org.springframework.stereotype.Service;

import java.util.List;

import com.loadingjr.chatapi.domain.dto.ChatResponseDTO;
import com.loadingjr.chatapi.domain.dto.CreateChatDTO;
import com.loadingjr.chatapi.domain.dto.RespondChatDTO;
import com.loadingjr.chatapi.domain.entity.Chat;
import com.loadingjr.chatapi.domain.entity.User;
import com.loadingjr.chatapi.domain.enums.ChatStatus;
import com.loadingjr.chatapi.exception.BadRequestException;
import com.loadingjr.chatapi.exception.ForbiddenException;
import com.loadingjr.chatapi.exception.NotFoundException;
import com.loadingjr.chatapi.repository.ChatRepository;
import com.loadingjr.chatapi.repository.UserRepository;
import com.loadingjr.chatapi.security.AuthService;


@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public ChatService(ChatRepository chatRepository,
                       UserRepository userRepository,
                       AuthService authService) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public Chat createChat(CreateChatDTO dto) {

        if (dto.requesterId().equals(dto.receiverId())) {
            throw new BadRequestException("Não pode criar chat consigo mesmo");
        }

        Long authenticatedUserId = authService.getAuthenticatedUserId();
        if (!authenticatedUserId.equals(dto.requesterId())) {
            throw new ForbiddenException("Você só pode abrir chat em seu próprio nome");
        }

        User requester = userRepository.findById(dto.requesterId())
                .orElseThrow(() -> new NotFoundException("Solicitante não encontrado"));

        User receiver = userRepository.findById(dto.receiverId())
                .orElseThrow(() -> new NotFoundException("Destinatário não encontrado"));

        chatRepository.findByUser1IdOrUser2Id(requester.getId(), requester.getId())
                .stream()
                .filter(existing -> existing.getStatus() == ChatStatus.PENDING || existing.getStatus() == ChatStatus.ACTIVE)
                .filter(existing ->
                        (existing.getUser1().getId().equals(requester.getId()) && existing.getUser2().getId().equals(receiver.getId())) ||
                        (existing.getUser1().getId().equals(receiver.getId()) && existing.getUser2().getId().equals(requester.getId()))
                )
                .findFirst()
                .ifPresent(existing -> {
                    throw new BadRequestException("Já existe um chat pendente ou ativo entre os usuários");
                });

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
            throw new BadRequestException("Chat não está pendente");
        }

        Long authenticatedUserId = authService.getAuthenticatedUserId();
        if (!chat.getUser2().getId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Apenas o usuário que recebeu a solicitação pode responder");
        }

        if (dto.accept()) {

            chatRepository.findActiveChatByUser(ChatStatus.ACTIVE, chat.getUser1())
                    .ifPresent(c -> {
                        throw new BadRequestException("Usuário 1 já possui chat ativo");
                    });

            chatRepository.findActiveChatByUser(ChatStatus.ACTIVE, chat.getUser2())
                    .ifPresent(c -> {
                        throw new BadRequestException("Usuário 2 já possui chat ativo");
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

        Long authenticatedUserId = authService.getAuthenticatedUserId();
        if (!chat.getUser1().getId().equals(authenticatedUserId) &&
                !chat.getUser2().getId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Você não participa deste chat");
        }

        if (chat.getStatus() != ChatStatus.ACTIVE) {
            throw new BadRequestException("Somente chats ativos podem ser encerrados");
        }

        chat.setStatus(ChatStatus.CLOSED);
        chat.setClosedAt(java.time.LocalDateTime.now());

        return chatRepository.save(chat);
    }
    
    public List<ChatResponseDTO> getChatsByUser(Long userId) {

        Long authenticatedUserId = authService.getAuthenticatedUserId();
        if (!authenticatedUserId.equals(userId)) {
            throw new ForbiddenException("Você só pode consultar seu próprio histórico");
        }

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
        Long authenticatedUserId = authService.getAuthenticatedUserId();

        return chatRepository.findByUser1IdOrUser2Id(
                authenticatedUserId,
                authenticatedUserId
        );
    }

}
