package com.loadingjr.chatapi.service;

import com.loadingjr.chatapi.domain.dto.AuthTokenResponseDTO;
import com.loadingjr.chatapi.domain.dto.LoginDTO;
import com.loadingjr.chatapi.domain.entity.User;
import com.loadingjr.chatapi.repository.UserRepository;
import com.loadingjr.chatapi.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void deveLancarErroQuandoUsuarioNaoEncontrado() {
        LoginDTO loginDTO = new LoginDTO("naoexiste", "senha");

        when(userRepository.findByUsername("naoexiste")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(loginDTO));

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(userRepository).findByUsername("naoexiste");
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    void deveLancarErroQuandoSenhaInvalida() {
        LoginDTO loginDTO = new LoginDTO("usuario", "senhaErrada");
        User user = new User();
        user.setUsername("usuario");
        user.setPassword("hashCorreto");

        when(userRepository.findByUsername("usuario")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senhaErrada", "hashCorreto")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(loginDTO));

        assertEquals("Senha inválida", exception.getMessage());
        verify(userRepository).findByUsername("usuario");
        verify(passwordEncoder).matches("senhaErrada", "hashCorreto");
        verifyNoInteractions(jwtService);
    }

    @Test
    void deveRetornarTokenQuandoLoginComSucesso() {
        LoginDTO loginDTO = new LoginDTO("usuario", "senhaCorreta");
        User user = new User();
        user.setId(1L);
        user.setUsername("usuario");
        user.setPassword("hashCorreto");

        when(userRepository.findByUsername("usuario")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senhaCorreta", "hashCorreto")).thenReturn(true);
        when(jwtService.generateToken(1L)).thenReturn("jwt-token");

        AuthTokenResponseDTO response = authService.login(loginDTO);

        assertEquals("jwt-token", response.token());
        verify(userRepository).findByUsername("usuario");
        verify(passwordEncoder).matches("senhaCorreta", "hashCorreto");
        verify(jwtService).generateToken(1L);
    }
}
