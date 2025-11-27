package com.producttrial.back.controller;

import com.producttrial.back.dto.AuthRequestDTO;
import com.producttrial.back.dto.ProductDTO;
import com.producttrial.back.entity.Product;
import com.producttrial.back.entity.User;
import com.producttrial.back.exception.GlobalExceptionHandler;
import com.producttrial.back.repository.ProductRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(GlobalExceptionHandler.class)
class ProductControllerIT {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Product product1;
    private Product product2;
    private User admin;
    private User user1;

    private String obtainToken(String email) throws Exception {
        AuthRequestDTO authRequestDTO = AuthRequestDTO.builder()
                .email(email)
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
        productRepository.deleteAll();
        userRepository.deleteAll();

        product1 = Product.builder()
                .name("Produit A")
                .code("C1")
                .price(10.00D)
                .createdAt(System.currentTimeMillis())
                .updatedAt(System.currentTimeMillis())
                .build();
        product1 = productRepository.save(product1);

        product2 = Product.builder()
                .name("Produit B")
                .code("C2")
                .price(10.00D)
                .createdAt(System.currentTimeMillis())
                .updatedAt(System.currentTimeMillis())
                .build();
        product2 = productRepository.save(product2);

        admin = User.builder()
                .email("admin@admin.com")
                .username("admin")
                .firstname("Admin")
                .password(passwordEncoder.encode("Mdp!1234"))
                .build();
        admin = userRepository.save(admin);

        user1 = User.builder()
                .email("test@test.fr")
                .username("test")
                .firstname("Test")
                .password(passwordEncoder.encode("Mdp!1234"))
                .build();
        user1 = userRepository.save(user1);
    }

    @Test
    void getAllProducts_returnsAllProducts() throws Exception {
        String token = obtainToken(admin.getEmail());
        mockMvc.perform(get("/products")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getAllProducts_withPagination_pageSize1() throws Exception {
        String token = obtainToken(admin.getEmail());
        mockMvc.perform(get("/products")
                        .param("page", "0")
                        .param("size", "1")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getAllProducts_sizeTooLarge_returnsBadRequest() throws Exception {
        String token = obtainToken(admin.getEmail());
        mockMvc.perform(get("/products")
                        .param("page", "0")
                        .param("size", "500")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllProducts_returnsAllProducts_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/products")
                        .header("Authorization", "Bearer ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getProduct_returnProduct() throws Exception {
        String token = obtainToken(admin.getEmail());
        mockMvc.perform(get("/products/{id}", product1.getId())
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Produit A"))
                .andExpect(jsonPath("$.code").value("C1"))
                .andExpect(jsonPath("$.price").value(10.00D));
    }
    @Test
    void getProduct_returnNotFound() throws Exception {
        String token = obtainToken(admin.getEmail());
        mockMvc.perform(get("/products/10")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getProduct_returnProduct_Unauthorized() throws Exception {
        mockMvc.perform(get("/products/10")
                        .header("Authorization", "Bearer ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void createProduct_returnsCreatedProduct() throws Exception {
        String token = obtainToken(admin.getEmail());
        ProductDTO productCreateDTO = ProductDTO.builder()
                .name("X2")
                .code("C3")
                .price(10.00D)
                .build();

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productCreateDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("X2"))
                .andExpect(jsonPath("$.code").value("C3"))
                .andExpect(jsonPath("$.price").value(10.00D));
    }

    @Test
    void createProduct_returnsBadRequest() throws Exception {
        String token = obtainToken(admin.getEmail());
        ProductDTO productCreateDTO = ProductDTO.builder()
                .name("X2")
                .price(-10.00D)
                .build();
        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productCreateDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.fields.code").value("Code is required"))
                .andExpect(jsonPath("$.fields.price").value("Price must be greater than 0"));
    }

    @Test
    void createProduct_returnsUnauthorized() throws Exception {
        ProductDTO productCreateDTO = ProductDTO.builder()
                .name("X2")
                .code("C3")
                .price(10.00D)
                .build();
        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productCreateDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void createProduct_returnsForbidden() throws Exception {
        String token = obtainToken(user1.getEmail());
        ProductDTO productCreateDTO = ProductDTO.builder()
                .name("X2")
                .code("C3")
                .price(10.00D)
                .build();
        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productCreateDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void updateProduct_returnsUpdatedProduct() throws Exception {
        String token = obtainToken(admin.getEmail());
        ProductDTO productUpdateDTO = ProductDTO.builder()
                .id(product1.getId())
                .name("X2")
                .code("C3")
                .price(10.00D)
                .build();

        mockMvc.perform(put("/products/{id}", product1.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdateDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("X2"))
                .andExpect(jsonPath("$.code").value("C3"))
                .andExpect(jsonPath("$.price").value(10.00D));
    }

    @Test
    void updateProduct_returnsNotFound() throws Exception {
        String token = obtainToken(admin.getEmail());
        ProductDTO productUpdateDTO = ProductDTO.builder()
                .id(10L)
                .name("X2")
                .code("C3")
                .price(10.00D)
                .build();

        mockMvc.perform(put("/products/{id}", 100L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdateDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateProduct_returnUnauthorized() throws Exception {
        ProductDTO productUpdateDTO = ProductDTO.builder()
                .id(product1.getId())
                .name("X2")
                .code("C3")
                .price(10.00D)
                .build();

        mockMvc.perform(put("/products/{id}", product1.getId())
                        .header("Authorization", "Bearer ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdateDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateProduct_returnForbidden() throws Exception {
        String token = obtainToken(user1.getEmail());
        ProductDTO productUpdateDTO = ProductDTO.builder()
                .id(product1.getId())
                .name("X2")
                .code("C3")
                .price(10.00D)
                .build();

        mockMvc.perform(put("/products/{id}", product1.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdateDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProduct_returnsNoContent() throws Exception {
        String token = obtainToken(admin.getEmail());
        mockMvc.perform(delete("/products/{id}", product1.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_returnsNotFound() throws Exception {
        String token = obtainToken(admin.getEmail());
        mockMvc.perform(delete("/products/{id}", 10L)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Product with id 10 not found"))
                .andExpect(jsonPath("$.path").value("/products/10"));
    }

    @Test
    void deleteProduct_returnsUnauthorized() throws Exception {
        mockMvc.perform(delete("/products/{id}", product1.getId())
                        .header("Authorization", "Bearer "))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void deleteProduct_returnsForbidden() throws Exception {
        String token = obtainToken(user1.getEmail());
        mockMvc.perform(delete("/products/{id}", product1.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }
}
