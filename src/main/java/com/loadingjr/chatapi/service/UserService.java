package com.loadingjr.chatapi.service;

import com.loadingjr.chatapi.domain.dto.UserRegisterDTO;
import com.loadingjr.chatapi.domain.entity.User;
import com.loadingjr.chatapi.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(UserRegisterDTO dto) {

        userRepository.findByUsername(dto.username())
                .ifPresent(u -> {
                    throw new RuntimeException("Username já existe");
                });

        User user = new User(dto.username(), dto.password());

        return userRepository.save(user);
    }
}
