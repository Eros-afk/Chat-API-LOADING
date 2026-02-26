package com.loadingjr.chatapi.exception;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String code,
        String message,
        String path
) {
}
