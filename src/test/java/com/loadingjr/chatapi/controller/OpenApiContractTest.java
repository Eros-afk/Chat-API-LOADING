package com.loadingjr.chatapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sendMessageDtoDeveExporApenasChatIdEContent() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.schemas.SendMessageDTO.required[0]").value("chatId"))
                .andExpect(jsonPath("$.components.schemas.SendMessageDTO.required[1]").value("content"))
                .andExpect(jsonPath("$.components.schemas.SendMessageDTO.properties.chatId.type").value("integer"))
                .andExpect(jsonPath("$.components.schemas.SendMessageDTO.properties.content.type").value("string"))
                .andExpect(jsonPath("$.components.schemas.SendMessageDTO.properties.senderId").doesNotExist());
    }

    @Test
    void createChatDtoDeveExporApenasReceiverId() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.schemas.CreateChatDTO.required[0]").value("receiverId"))
                .andExpect(jsonPath("$.components.schemas.CreateChatDTO.properties.receiverId.type").value("integer"))
                .andExpect(jsonPath("$.components.schemas.CreateChatDTO.properties.requesterId").doesNotExist());
    }
}
