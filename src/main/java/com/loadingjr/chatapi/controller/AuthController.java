package com.loadingjr.chatapi.controller;

import com.loadingjr.chatapi.domain.dto.LoginDTO;
import com.loadingjr.chatapi.domain.entity.User;
import com.loadingjr.chatapi.repository.UserRepository;
import com.loadingjr.chatapi.security.JwtService;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Operation(summary = "Faz o login do usuário")
    @PostMapping("/login")
    public String login(@RequestBody LoginDTO dto) {

        User user = userRepository.findByUsername(dto.username())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        return jwtService.generateToken(user.getId());
    }
}