package com.loadingjr.chatapi.domain.dto;

import java.time.LocalDateTime;

public record ChatResponseDTO(
        Long id,
        String user1,
        String user2,
        String status,
        LocalDateTime createdAt
) {}