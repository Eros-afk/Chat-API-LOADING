package com.loadingjr.chatapi.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

public record SendMessageDTO(

        @Schema(description = "ID do chat onde a mensagem será enviada", example = "10")
        @NotNull(message = "Chat é obrigatório")
        Long chatId,

        @Schema(description = "Conteúdo textual da mensagem", example = "Olá! Tudo bem?")
        @NotBlank(message = "Mensagem não pode estar vazia")
        String content

) {}
