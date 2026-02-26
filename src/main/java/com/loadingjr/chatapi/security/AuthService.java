package com.loadingjr.chatapi.security;

import com.loadingjr.chatapi.exception.ForbiddenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthService {

    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ForbiddenException("Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Long userId) {
            return userId;
        }

        if (principal instanceof String stringPrincipal) {
            try {
                return Long.parseLong(stringPrincipal);
            } catch (NumberFormatException e) {
                throw new ForbiddenException("Credenciais inválidas");
            }
        }

        throw new ForbiddenException("Credenciais inválidas");
    }
}
