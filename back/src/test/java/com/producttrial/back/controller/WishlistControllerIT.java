package com.producttrial.back.controller;

import com.producttrial.back.dto.AuthRequestDTO;
import com.producttrial.back.dto.WishlistItemDTO;
import com.producttrial.back.entity.Product;
import com.producttrial.back.entity.User;
import com.producttrial.back.entity.WishlistItem;
import com.producttrial.back.exception.GlobalExceptionHandler;
import com.producttrial.back.repository.ProductRepository;
import com.producttrial.back.repository.UserRepository;
import com.producttrial.back.repository.WishlistItemRepository;
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
class WishlistControllerIT {
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
    private WishlistItemRepository wishlistItemRepository;

    private Product product1;
    private Product product2;
    private WishlistItem wishlistItem;

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
        wishlistItemRepository.deleteAll();

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
        wishlistItem = WishlistItem.builder()
                .user(user)
                .product(product1)
                .createdAt(now)
                .updatedAt(now)
                .build();
        wishlistItem = wishlistItemRepository.save(wishlistItem);
    }

    @Test
    void getWishlist_returnAllWishlistItemOfUser() throws Exception {
        String token = obtainToken();
        mockMvc.perform(get("/wishlist")
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
    void getWishlist_sizeTooLarge_returnsBadRequest() throws Exception {
        String token = obtainToken();
        mockMvc.perform(get("/wishlist")
                        .param("page", "0")
                        .param("size", "500")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWishlistItem_returnProduct() throws Exception {
        String token = obtainToken();
        mockMvc.perform(get("/wishlist/{id}", wishlistItem.getId())
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(product1.getId()));
    }
    @Test
    void getWishlistItem_returnNotFound() throws Exception {
        String token = obtainToken();
        mockMvc.perform(get("/wishlist/10")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getWishlistItem_returnWishlistItem_Unauthorized() throws Exception {
        mockMvc.perform(get("/wishlist/10")
                        .header("Authorization", "Bearer ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
    @Test
    void createWishlistItem_returnsCreatedWishlistItem() throws Exception {
        String token = obtainToken();
        WishlistItemDTO wishlistItemDTO = WishlistItemDTO.builder()
                .productId(product2.getId())
                .build();

        mockMvc.perform(post("/wishlist")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wishlistItemDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(product2.getId()));
    }

    @Test
    void createWishlistItem_whenExist_ReturnConflict() throws Exception {
        String token = obtainToken();
        WishlistItemDTO wishlistItemDTO = WishlistItemDTO.builder()
                .productId(product1.getId())
                .build();

        mockMvc.perform(post("/wishlist")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wishlistItemDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void createWishlistItem_returnsBadRequest() throws Exception {
        String token = obtainToken();
        WishlistItemDTO wishlistItemDTO = WishlistItemDTO.builder()
                .productId(null)
                .build();
        mockMvc.perform(post("/wishlist")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wishlistItemDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.fields.productId").value("Product id is required"));
    }

    @Test
    void createWishlistItem_returnsUnauthorized() throws Exception {
        WishlistItemDTO wishlistItemDTO = WishlistItemDTO.builder()
                .productId(product1.getId())
                .build();

        mockMvc.perform(post("/wishlist")
                        .header("Authorization", "Bearer ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wishlistItemDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void deleteWishlistItem_returnsNoContent() throws Exception {
        String token = obtainToken();
        mockMvc.perform(delete("/wishlist/{id}", wishlistItem.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteWishlistItem_returnsNotFound() throws Exception {
        String token = obtainToken();
        mockMvc.perform(delete("/wishlist/{id}", 10L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Wishlist item with id 10 not found"))
                .andExpect(jsonPath("$.path").value("/wishlist/10"));
    }

    @Test
    void deleteWishlistItem_returnsUnauthorized() throws Exception {
        mockMvc.perform(delete("/wishlist/{id}", wishlistItem.getId())
                        .header("Authorization", "Bearer "))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
