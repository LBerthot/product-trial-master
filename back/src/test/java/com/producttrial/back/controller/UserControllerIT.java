package com.producttrial.back.controller;

import com.producttrial.back.dto.AuthRequestDTO;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user1;

    private String obtainAdminToken() throws Exception {
        AuthRequestDTO authRequestDTO = AuthRequestDTO.builder()
                .email("test@test.fr")
                .password("Mdp!1234")
                .build();

        String response = mockMvc.perform(post("/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asString();
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();

        userRepository.deleteAll();
        userRepository.deleteAll();

        user1 = User.builder()
                .email("test@test.fr")
                .username("test")
                .firstname("Test")
                .password(passwordEncoder.encode("Mdp!1234"))
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
                .password("test")
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
        String token = obtainAdminToken();
        mockMvc.perform(get("/account/{id}", user1.getId())
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.fr"))
                .andExpect(jsonPath("$.username").value("test"));
    }

    @Test
    void getUser_returnUser_notFound() throws Exception {
        String token = obtainAdminToken();
        mockMvc.perform(get("/account/10")
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getUser_returnUser_Unauthorized() throws Exception {
        mockMvc.perform(get("/account/10")
                        .header("Authorization", "Bearer ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
