package com.loadingjr.chatapi.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserProvider {

    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Long userId) {
            return userId;
        }

        if (principal instanceof String userId) {
            try {
                return Long.parseLong(userId);
            } catch (NumberFormatException ex) {
                throw new RuntimeException("Principal de autenticação inválido");
            }
        }

        throw new RuntimeException("Principal de autenticação inválido");
    }

    public Long getCurrentUserId() {
        return getAuthenticatedUserId();
    }
}
