package com.loadingjr.chatapi.controller;

import com.loadingjr.chatapi.domain.dto.CreateChatDTO;
import com.loadingjr.chatapi.domain.entity.Chat;
import com.loadingjr.chatapi.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.loadingjr.chatapi.domain.dto.RespondChatDTO;
import com.loadingjr.chatapi.domain.dto.RespondChatDTO;


@RestController
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public Chat create(@RequestBody @Valid CreateChatDTO dto) {
        return chatService.createChat(dto);
    }
    
    @PostMapping("/respond")
    public Chat respond(@RequestBody @Valid RespondChatDTO dto) {
        return chatService.respondToChat(dto);
    }
    
    @PostMapping("/close/{chatId}")
    public Chat close(@PathVariable Long chatId) {
        return chatService.closeChat(chatId);
    }


}
