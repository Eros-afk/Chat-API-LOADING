package com.loadingjr.chatapi.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserProvider {

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Long userId) {
            return userId;
        }

        if (principal instanceof String principalAsString) {
            try {
                return Long.parseLong(principalAsString);
            } catch (NumberFormatException ex) {
                throw new RuntimeException("Principal inválido no contexto de autenticação");
            }
        }

        throw new RuntimeException("Principal inválido no contexto de autenticação");
    }
}
