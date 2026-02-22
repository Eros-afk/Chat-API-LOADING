package com.loadingjr.chatapi.service;

import com.loadingjr.chatapi.domain.dto.UserRegisterDTO;
import com.loadingjr.chatapi.domain.entity.User;
import com.loadingjr.chatapi.exception.BusinessRuleException;
import com.loadingjr.chatapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(UserRegisterDTO dto) {

        userRepository.findByUsername(dto.username())
                .ifPresent(u -> {
                    throw new BusinessRuleException("Username já existe");
                });

        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));

        return userRepository.save(user);
    }
}
