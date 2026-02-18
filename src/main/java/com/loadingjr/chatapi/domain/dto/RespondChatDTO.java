package com.loadingjr.chatapi.domain.dto;

import jakarta.validation.constraints.NotNull;

public record RespondChatDTO(

        @NotNull
        Long chatId,

        @NotNull
        Boolean accept

) {}
