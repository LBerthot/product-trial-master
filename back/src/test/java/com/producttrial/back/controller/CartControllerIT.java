package com.producttrial.back.controller;

import com.producttrial.back.dto.AuthRequestDTO;
import com.producttrial.back.dto.CartItemDTO;
import com.producttrial.back.entity.CartItem;
import com.producttrial.back.entity.Product;
import com.producttrial.back.entity.User;
import com.producttrial.back.exception.GlobalExceptionHandler;
import com.producttrial.back.repository.CartItemRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(GlobalExceptionHandler.class)
class CartControllerIT {
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

    @Autowired
    private CartItemRepository cartItemRepository;

    private Product product1;
    private Product product2;
    private CartItem cartItem;

    private String obtainToken() throws Exception {
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
        productRepository.deleteAll();
        userRepository.deleteAll();
        cartItemRepository.deleteAll();

        Long now = System.currentTimeMillis();
        product1 = Product.builder()
                .name("Produit A")
                .code("C1")
                .price(10.00D)
                .createdAt(now)
                .updatedAt(now)
                .build();
        product1 = productRepository.save(product1);
        product2 = Product.builder()
                .name("Produit B")
                .code("C2")
                .price(10.00D)
                .createdAt(now)
                .updatedAt(now)
                .build();
        product2 = productRepository.save(product2);
        User user = User.builder()
                .email("test@test.fr")
                .username("test")
                .firstname("Test")
                .password(passwordEncoder.encode("Mdp!1234"))
                .build();
        user = userRepository.save(user);
        cartItem = CartItem.builder()
                .user(user)
                .product(product1)
                .createdAt(now)
                .updatedAt(now)
                .quantity(2)
                .build();
        cartItem = cartItemRepository.save(cartItem);
    }

    @Test
    void getCart_returnAllCartItemOfUser() throws Exception {
        String token = obtainToken();
        mockMvc.perform(get("/cart")
                        .param("page", "0")
                        .param("size", "1")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getCart_sizeTooLarge_returnsBadRequest() throws Exception {
        String token = obtainToken();
        mockMvc.perform(get("/cart")
                        .param("page", "0")
                        .param("size", "500")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCartItem_returnProduct() throws Exception {
        String token = obtainToken();
        mockMvc.perform(get("/cart/{id}", cartItem.getId())
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(product1.getId()))
                .andExpect(jsonPath("$.quantity").value(cartItem.getQuantity()));
    }
    @Test
    void getCartItem_returnNotFound() throws Exception {
        String token = obtainToken();
        mockMvc.perform(get("/cart/10")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getWishlistItem_returnWishlistItem_Unauthorized() throws Exception {
        mockMvc.perform(get("/cart/10")
                        .header("Authorization", "Bearer ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
    @Test
    void createCartItem_returnsCreatedCartItem() throws Exception {
        String token = obtainToken();
        CartItemDTO cartItemDTO = CartItemDTO.builder()
                .productId(product2.getId())
                .quantity(2)
                .build();

        mockMvc.perform(post("/cart")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(product2.getId()))
                .andExpect(jsonPath("$.quantity").value(cartItemDTO.getQuantity()));
    }

    @Test
    void createCartItem_returnsUpdateQuantity() throws Exception {
        String token = obtainToken();
        CartItemDTO cartItemDTO = CartItemDTO.builder()
                .productId(product1.getId())
                .quantity(2)
                .build();

        mockMvc.perform(post("/cart")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(product1.getId()))
                .andExpect(jsonPath("$.quantity").value(cartItem.getQuantity() + cartItemDTO.getQuantity()));
    }

    @Test
    void createCartItem_returnsBadRequest() throws Exception {
        String token = obtainToken();
        CartItemDTO cartItemDTO = CartItemDTO.builder()
                .quantity(0)
                .build();
        mockMvc.perform(post("/cart")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.fields.productId").value("Product id is required"))
                .andExpect(jsonPath("$.fields.quantity").value("Quantity must be at least 1"));
    }

    @Test
    void createCartItem_returnsUnauthorized() throws Exception {
        CartItemDTO cartItemDTO = CartItemDTO.builder()
                .productId(product1.getId())
                .quantity(2)
                .build();

        mockMvc.perform(post("/cart")
                        .header("Authorization", "Bearer ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void deleteCartItem_returnsNoContent() throws Exception {
        String token = obtainToken();
        mockMvc.perform(delete("/cart/{id}", cartItem.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCartItem_returnsNotFound() throws Exception {
        String token = obtainToken();
        mockMvc.perform(delete("/cart/{id}", 100L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Cart item with id 100 not found"))
                .andExpect(jsonPath("$.path").value("/cart/100"));
    }

    @Test
    void deleteCartItem_returnsUnauthorized() throws Exception {
        mockMvc.perform(delete("/cart/{id}", cartItem.getId())
                        .header("Authorization", "Bearer "))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
