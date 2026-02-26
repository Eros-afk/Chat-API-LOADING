package com.loadingjr.chatapi.controller;

import com.loadingjr.chatapi.domain.entity.User;
import com.loadingjr.chatapi.exception.BusinessRuleException;
import com.loadingjr.chatapi.service.UserService;
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

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldReturnConflictWhenBusinessRuleIsViolated() throws Exception {
        when(userService.register(any())).thenThrow(new BusinessRuleException("Username já existe"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"joao","password":"123456"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"))
                .andExpect(jsonPath("$.message").value("Username já existe"))
                .andExpect(jsonPath("$.path").value("/users"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"ab","password":"123"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.path").value("/users"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldCreateUserWhenPayloadIsValid() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("joao");
        user.setPassword("hash");

        when(userService.register(any())).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"joao","password":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("joao"));
    }
}
