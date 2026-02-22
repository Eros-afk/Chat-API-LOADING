package com.loadingjr.chatapi.domain.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateChatDTO(

        @Schema(description = "ID do usuário destinatário", example = "2")
        @NotNull(message = "ID do usuário destinatário é obrigatório")
        Long receiverId

) {}
