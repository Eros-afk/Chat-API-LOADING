package com.loadingjr.chatapi.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthenticatedUserProviderTest {

    private final AuthenticatedUserProvider authenticatedUserProvider = new AuthenticatedUserProvider();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnCurrentUserIdWhenAuthenticatedWithStringPrincipal() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("42", null, Collections.emptyList())
        );

        Long userId = authenticatedUserProvider.getCurrentUserId();

        assertEquals(42L, userId);
    }

    @Test
    void shouldThrowWhenUserIsNotAuthenticated() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticatedUserProvider.getCurrentUserId());

        assertEquals("Usuário não autenticado", exception.getMessage());
    }
}
