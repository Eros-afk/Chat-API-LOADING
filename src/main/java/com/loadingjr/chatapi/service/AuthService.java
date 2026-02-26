package com.loadingjr.chatapi.service;

import com.loadingjr.chatapi.domain.dto.AuthTokenResponseDTO;
import com.loadingjr.chatapi.domain.dto.LoginDTO;
import com.loadingjr.chatapi.domain.entity.User;
import com.loadingjr.chatapi.exception.InvalidCredentialsException;
import com.loadingjr.chatapi.exception.NotFoundException;
import com.loadingjr.chatapi.repository.UserRepository;
import com.loadingjr.chatapi.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthTokenResponseDTO login(LoginDTO dto) {
        User user = userRepository.findByUsername(dto.username())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Senha inválida");
        }

        String token = jwtService.generateToken(user.getId());
        return new AuthTokenResponseDTO(token);
    }
}
