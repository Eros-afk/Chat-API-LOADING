package com.loadingjr.chatapi.controller;

import com.loadingjr.chatapi.domain.dto.AuthTokenResponseDTO;
import com.loadingjr.chatapi.exception.InvalidCredentialsException;
import com.loadingjr.chatapi.exception.NotFoundException;
import com.loadingjr.chatapi.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        when(authService.login(any())).thenThrow(new NotFoundException("Usuário não encontrado"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"inexistente","password":"123456"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Usuário não encontrado"))
                .andExpect(jsonPath("$.path").value("/auth/login"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnUnauthorizedWhenPasswordIsInvalid() throws Exception {
        when(authService.login(any())).thenThrow(new InvalidCredentialsException("Senha inválida"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"teste","password":"senhaErrada"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("Senha inválida"))
                .andExpect(jsonPath("$.path").value("/auth/login"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnTokenWhenCredentialsAreValid() throws Exception {
        when(authService.login(any())).thenReturn(new AuthTokenResponseDTO("jwt-token"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"teste","password":"senhaCorreta"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }
}
