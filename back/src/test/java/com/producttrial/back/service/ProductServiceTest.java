package com.producttrial.back.service;

import com.producttrial.back.entity.Product;
import com.producttrial.back.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void findAll_returnsListFromRepository() {
        when(productRepository.findAll()).thenReturn(List.of(new Product(), new Product()));

        List<Product> result = productService.findAll();
        assertEquals(2, result.size(), "list should contain 1 element");
    }
    @Test
    void findById_whenPresent_returnsOptionalProduct() {
        Product product = new Product();
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.findById(1L);
        assertTrue(result.isPresent(), "result should be present");
        assertEquals(1L, result.get().getId(), "id should be 1");
    }

    @Test
    void findById_whenNotPresent_returnsEmptyOptional() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.findById(2L);
        assertTrue(result.isEmpty(), "result should be empty");
    }

    @Test
    void save_setsTimestampsAndReturnsSavedProduct() {
        Product product = Product.builder()
                .name("X")
                .code("C1")
                .price(10.00D)
                .build();

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product p = invocation.getArgument(0);
                    p.setId(1L);
                    return p;
                });

        // Pour vérifier que les timestamps sont dans le bon interval
        long before = System.currentTimeMillis();
        Product saved = productService.save(product);
        long after = System.currentTimeMillis();

        assertNotNull(saved.getId(), "id should not be null");
        assertEquals(1L, saved.getId(), "id should be 1");
        assertEquals("X", saved.getName(), "name should be X");
        assertEquals("C1", saved.getCode(),"code should be C1");
        assertEquals(10.00D, saved.getPrice(), "price should be 10.00");

        assertNotNull(saved.getCreatedAt(), "createdAt should not be null");
        assertNotNull(saved.getUpdatedAt(), "updatedAt should not be null");
        assertTrue(saved.getCreatedAt() >= before && saved.getCreatedAt() <= after + 1000,
                "createdAt must be between before and after");
        assertTrue(saved.getUpdatedAt() >= before && saved.getUpdatedAt() <= after + 1000,
                "updatedAt must be between before and after");
    }

    @Test
    void update_existingProduct_updatesFieldsAndTimestamps() throws InterruptedException {
        Long id = 1L;
        Product product = Product.builder()
                .id(id)
                .name("Old")
                .code("OLD")
                .price(5.0D)
                .createdAt(System.currentTimeMillis() - 10_000)
                .updatedAt(System.currentTimeMillis() - 10_000)
                .build();

        Product changed = Product.builder()
                .name("X2")
                .build();


        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product saved = productService.save(product);
        Long updateOd = saved.getUpdatedAt();
        Thread.sleep(500); // pas ouf mais permet de s'assurer que le updateAt soit différent

        Product updated = productService.update(saved.getId(), changed);

        assertEquals("X2", updated.getName(), "name should be X2");
        assertEquals(product.getCode(), updated.getCode(), "code should be unchanged");
        assertEquals(saved.getCreatedAt(), updated.getCreatedAt(), "createdAt should be the same");
        assertTrue(updated.getUpdatedAt() > updateOd, "updated should be greater");
    }

    @Test
    void update_nonExistingProduct_throwsRuntimeException() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());
        Product product = Product.builder()
                .name("X2")
                .build();

        assertThrows(RuntimeException.class, () -> productService.update(2L, product));
    }

    @Test
    void delete_callsRepositoryDeleteById() {
        when(productRepository.existsById(5L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(5L);

        productService.delete(5L);

        verify(productRepository, times(1)).deleteById(5L);
    }

}
