package com.loadingjr.chatapi.controller;

import com.loadingjr.chatapi.domain.dto.MessageResponseDTO;
import com.loadingjr.chatapi.domain.dto.SendMessageDTO;
import com.loadingjr.chatapi.domain.entity.Message;
import com.loadingjr.chatapi.service.MessageService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

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
    
    @GetMapping("/chat/{chatId}")
    public List<MessageResponseDTO> getByChat(@PathVariable Long chatId) {
        return messageService.getMessagesByChat(chatId);
    }

}
