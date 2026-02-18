package com.loadingjr.chatapi.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMessageDTO(

        @NotNull
        Long chatId,

        @NotNull
        Long senderId,

        @NotBlank
        String content

) {}
