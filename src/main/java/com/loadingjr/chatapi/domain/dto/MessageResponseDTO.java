package com.loadingjr.chatapi.domain.dto;

import java.time.LocalDateTime;

public record MessageResponseDTO(
		Long id,
		String content,
		LocalDateTime createdAt,
		UserResponseDTO sender
) {}
