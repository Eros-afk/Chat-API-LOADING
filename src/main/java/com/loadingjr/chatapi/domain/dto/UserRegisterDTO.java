package com.loadingjr.chatapi.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterDTO(

        @NotBlank(message = "Username é obrigatório")
        @Size(min = 3, max = 20)
        String username,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6)
        String password

) {}
