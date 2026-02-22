package com.loadingjr.chatapi.controller;

import com.loadingjr.chatapi.domain.dto.AuthTokenResponseDTO;
import com.loadingjr.chatapi.domain.dto.LoginDTO;
import com.loadingjr.chatapi.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Faz o login do usuário")
    @PostMapping("/login")
    public AuthTokenResponseDTO login(@RequestBody LoginDTO dto) {
        return authService.login(dto);
    }
}
