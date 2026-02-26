package com.loadingjr.chatapi.service;

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
import com.loadingjr.chatapi.security.AuthenticatedUserProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public ChatService(ChatRepository chatRepository,
                       UserRepository userRepository,
                       AuthenticatedUserProvider authenticatedUserProvider) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public Chat createChat(CreateChatDTO dto) {

        Long authenticatedUserId = authenticatedUserProvider.getAuthenticatedUserId();

        if (authenticatedUserId.equals(dto.receiverId())) {
            throw new BusinessRuleException("Não pode criar chat consigo mesmo");
        }

        User requester = userRepository.findById(authenticatedUserId)
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

        Long authenticatedUserId = authenticatedUserProvider.getAuthenticatedUserId();

        if (!chat.getUser1().getId().equals(authenticatedUserId) &&
            !chat.getUser2().getId().equals(authenticatedUserId)) {
            throw new BusinessRuleException("Você não pode responder este chat");
        }

        if (!chat.getUser2().getId().equals(authenticatedUserId)) {
            throw new BusinessRuleException("Apenas o destinatário pode responder a solicitação");
        }

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
            chat.setClosedAt(java.time.LocalDateTime.now());
        }

        return chatRepository.save(chat);
    }

    public Chat closeChat(Long chatId) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException("Chat não encontrado"));

        Long authenticatedUserId = authenticatedUserProvider.getAuthenticatedUserId();

        if (!chat.getUser1().getId().equals(authenticatedUserId) &&
            !chat.getUser2().getId().equals(authenticatedUserId)) {
            throw new BusinessRuleException("Você não participa deste chat");
        }

        if (chat.getStatus() != ChatStatus.ACTIVE) {
            throw new BusinessRuleException("Somente chats ativos podem ser encerrados");
        }

        chat.setStatus(ChatStatus.CLOSED);
        chat.setClosedAt(java.time.LocalDateTime.now());

        return chatRepository.save(chat);
    }

    public List<ChatResponseDTO> getChatsByUser(Long userId) {

        Long authenticatedUserId = authenticatedUserProvider.getAuthenticatedUserId();

        if (!authenticatedUserId.equals(userId)) {
            throw new BusinessRuleException("Você só pode visualizar o seu próprio histórico");
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

        Long authenticatedUserId = authenticatedUserProvider.getAuthenticatedUserId();

        return chatRepository.findByUser1IdOrUser2Id(
                authenticatedUserId,
                authenticatedUserId
        );
    }

}
