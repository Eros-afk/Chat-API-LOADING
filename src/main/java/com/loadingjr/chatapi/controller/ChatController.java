package com.loadingjr.chatapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loadingjr.chatapi.domain.dto.ChatResponseDTO;
import com.loadingjr.chatapi.domain.dto.CreateChatDTO;
import com.loadingjr.chatapi.domain.dto.RespondChatDTO;
import com.loadingjr.chatapi.domain.entity.Chat;
import com.loadingjr.chatapi.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    
    @Operation(summary = "Cria um novo chat")
    @PostMapping
    public Chat create(@RequestBody @Valid CreateChatDTO dto) {
        return chatService.createChat(dto);
    }
    
    @Operation(summary = "Aceita ou nega a Requisição")
    @PostMapping("/respond")
    public Chat respond(@RequestBody @Valid RespondChatDTO dto) {
        return chatService.respondToChat(dto);
    }
    
    @Operation(summary = "Fecha o chat pelo Id")
    @PostMapping("/close/{chatId}")
    public Chat close(@PathVariable Long chatId) {
        return chatService.closeChat(chatId);
    }
    
    @Operation(summary = "Buscas os chats do usuário")
    @GetMapping("/user/{userId}")
    public List<ChatResponseDTO> getChatsByUser(@PathVariable Long userId) {
        return chatService.getChatsByUser(userId);
    }
    
    @Operation(summary = "Buscas os chats do usuário")
    @GetMapping("/my-chats")
    public List<Chat> getMyChats() {
        return chatService.getMyChats();
    }
}
