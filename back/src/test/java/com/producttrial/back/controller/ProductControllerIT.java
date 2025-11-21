package com.producttrial.back.controller;

import com.producttrial.back.dto.ProductDTO;
import com.producttrial.back.entity.Product;
import com.producttrial.back.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ProductControllerIT {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        productRepository.deleteAll();

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
    }

    @Test
    void getAllProducts_returnsAllProducts() throws Exception {
        mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getAllProducts_withPagination_pageSize1() throws Exception {
        mockMvc.perform(get("/products")
                        .param("page", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getAllProducts_sizeTooLarge_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/products")
                        .param("page", "0")
                        .param("size", "500")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProduct_returnProduct() throws Exception {
        mockMvc.perform(get("/products/{id}", product1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Produit A"))
                .andExpect(jsonPath("$.code").value("C1"))
                .andExpect(jsonPath("$.price").value(10.00D));
    }
    @Test
    void getProduct_returnProduct_notFound() throws Exception {
        mockMvc.perform(get("/products/10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_returnsCreatedProduct() throws Exception {
        ProductDTO productCreateDTO = ProductDTO.builder()
                .name("X2")
                .code("C3")
                .price(10.00D)
                .build();

        mockMvc.perform(post("/products")
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
        ProductDTO productCreateDTO = ProductDTO.builder()
                .name("X2")
                .price(-10.00D)
                .build();
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productCreateDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProduct_returnsUpdatedProduct() throws Exception {
        ProductDTO productUpdateDTO = ProductDTO.builder()
                .id(product1.getId())
                .name("X2")
                .code("C3")
                .price(10.00D)
                .build();

        mockMvc.perform(put("/products/{id}", product1.getId())
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
        ProductDTO productUpdateDTO = ProductDTO.builder()
                .id(10L)
                .name("X2")
                .code("C3")
                .price(10.00D)
                .build();

        mockMvc.perform(put("/products/{id}", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdateDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/products/{id}", product1.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/products/{id}", 10L))
                .andExpect(status().isInternalServerError());
    }
}
