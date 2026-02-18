package com.loadingjr.chatapi.controller;

import com.loadingjr.chatapi.domain.dto.UserRegisterDTO;
import com.loadingjr.chatapi.domain.entity.User;
import com.loadingjr.chatapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User register(@RequestBody @Valid UserRegisterDTO dto) {
        return userService.register(dto);
    }
}
