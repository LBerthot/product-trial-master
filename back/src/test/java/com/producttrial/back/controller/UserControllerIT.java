package com.producttrial.back.controller;

import com.producttrial.back.dto.UserCreateDTO;
import com.producttrial.back.entity.User;
import com.producttrial.back.exception.GlobalExceptionHandler;
import com.producttrial.back.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(GlobalExceptionHandler.class)
class UserControllerIT {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User user1;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        userRepository.deleteAll();

        user1 = User.builder()
                .email("test@test.fr")
                .username("test")
                .firstname("Test")
                .password("Test!123")
                .build();
        user1 = userRepository.save(user1);
    }

    @Test
    void createUser_returnsCreatedUser() throws Exception {
        UserCreateDTO userCreateDTO = UserCreateDTO.builder()
                .email("test2@test.fr")
                .username("test2")
                .firstname("Test2")
                .password("Test!123")
                .build();

        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test2@test.fr"))
                .andExpect(jsonPath("$.username").value("test2"));
    }

    @Test
    void createUser_returnsBadRequest() throws Exception {
        UserCreateDTO userCreateDTO = UserCreateDTO.builder()
                .email("")
                .username("test2")
                .firstname("Test2")
                .password("")
                .build();

        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.fields.email").value("Email is required"))
                .andExpect(jsonPath("$.fields.password").value("Password must contain at least one uppercase letter, one special character and one digit"));
    }

    @Test
    void getUser_returnUser() throws Exception {
        mockMvc.perform(get("/account/{id}", user1.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.fr"))
                .andExpect(jsonPath("$.username").value("test"));
    }

    @Test
    void getUser_returnUser_notFound() throws Exception {
        mockMvc.perform(get("/account/10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
