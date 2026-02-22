package com.loadingjr.chatapi.domain.dto;

import jakarta.validation.constraints.NotNull;

public record RespondChatDTO(

        @NotNull(message = "Id do chat é obrigatório")
        Long chatId,

        @NotNull (message = "É necessário aceitar ou recusar")
        Boolean accept

) {}
