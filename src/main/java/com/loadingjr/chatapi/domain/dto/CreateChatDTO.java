package com.loadingjr.chatapi.domain.dto;

import jakarta.validation.constraints.NotNull;

public record CreateChatDTO(

        @NotNull(message = "ID do usuário solicitante é obrigatório")
        Long requesterId,

        @NotNull(message = "ID do usuário destinatário é obrigatório")
        Long receiverId

) {}
