package com.loadingjr.chatapi.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMessageDTO(

        @NotNull(message = "Chat é obrigatório")
        Long chatId,

        @NotNull(message = "Sender é obrigatório")
        Long senderId,

        @NotBlank(message = "Mensagem não pode estar vazia")
        String content

) {}
