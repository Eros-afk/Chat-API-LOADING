package com.loadingjr.chatapi.controller;

import com.loadingjr.chatapi.domain.dto.MessageResponseDTO;
import com.loadingjr.chatapi.domain.dto.SendMessageDTO;
import com.loadingjr.chatapi.domain.entity.Message;
import com.loadingjr.chatapi.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public Message send(@RequestBody @Valid SendMessageDTO dto) {
        return messageService.sendMessage(dto);
    }
    
    @Operation(summary = "Mostra as mensagens pelo Id do chat")
    @GetMapping("/chat/{chatId}")
    public List<MessageResponseDTO> getByChat(@PathVariable Long chatId) {
        return messageService.getMessagesByChat(chatId);
    }
    
    @Operation(summary = "Mostra as mensagnes por página")
    @GetMapping("/{chatId}/page")
    public Page<MessageResponseDTO> getMessages(
            @PathVariable Long chatId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        return messageService.getMessages(chatId, pageable);
    }


}
