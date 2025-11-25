package com.producttrial.back.controller;

import com.producttrial.back.dto.AuthRequestDTO;
import com.producttrial.back.entity.User;
import com.producttrial.back.exception.GlobalExceptionHandler;
import com.producttrial.back.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(GlobalExceptionHandler.class)
class AuthControllerIT {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private String rawPassword;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();

        userRepository.deleteAll();
        rawPassword = "Test!123";

        user1 = User.builder()
                .email("test@test.fr")
                .username("test")
                .firstname("Test")
                .password(passwordEncoder.encode(rawPassword))
                .build();
        user1 = userRepository.save(user1);
    }

    @Test
    void login_returnsToken() throws Exception {
        AuthRequestDTO authRequestDTO = AuthRequestDTO.builder()
                .email(user1.getEmail())
                .password(rawPassword)
                .build();

        mockMvc.perform(post("/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_returnsUnauthorized() throws Exception {
        AuthRequestDTO authRequestDTO = AuthRequestDTO.builder()
                .email(user1.getEmail())
                .password("bad password")
                .build();

        mockMvc.perform(post("/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}
