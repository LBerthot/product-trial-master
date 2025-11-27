package com.producttrial.back.service;

import com.producttrial.back.dto.CartItemDTO;
import com.producttrial.back.entity.CartItem;
import com.producttrial.back.entity.Product;
import com.producttrial.back.entity.User;
import com.producttrial.back.exception.CartItemNotFoundException;
import com.producttrial.back.exception.ProductNotFoundException;
import com.producttrial.back.exception.UserNotFoundException;
import com.producttrial.back.repository.CartItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private IUserService userService;
    @Mock
    private IProductService productService;

    @InjectMocks
    private CartItemServiceImpl cartItemService;

    private User user;
    private Product product;
    private CartItemDTO cartItemDTO;
    Long itemId = 1L;
    Long userId = 1L;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("test")
                .firstname("Test")
                .email("test@test.fr")
                .password("test")
                .build();

        product = Product.builder()
                .id(1L)
                .name("X")
                .code("C1")
                .price(10.00D)
                .build();

        cartItemDTO = CartItemDTO.builder()
                .productId(1L)
                .quantity(2)
                .build();
    }
    @Test
    void findByUserId_returnsListFromRepository() {
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(new CartItem(), new CartItem()));

        List<CartItem> result = cartItemRepository.findByUserId(1L);
        assertEquals(2, result.size(), "list should contain 2 element");
    }

    @Test
    void save_setsTimestampsAndReturnsSavedProduct() {
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(productService.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> {
            CartItem c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });


        // Pour vérifier que les timestamps sont dans le bon interval
        long before = System.currentTimeMillis();
        CartItem saved = cartItemService.save(cartItemDTO, 1L);
        long after = System.currentTimeMillis();

        assertNotNull(saved.getId(), "id should not be null");
        assertEquals(1L, saved.getId(), "id should be 1");
        assertEquals("X", saved.getProduct().getName(), "name should be X");
        assertEquals("test@test.fr", saved.getUser().getEmail(),"email should be test@test.fr");
        assertEquals(2, saved.getQuantity(), "quantity should be 2");

        assertNotNull(saved.getCreatedAt(), "createdAt should not be null");
        assertNotNull(saved.getUpdatedAt(), "updatedAt should not be null");
        assertTrue(saved.getCreatedAt() >= before && saved.getCreatedAt() <= after + 1000,
                "createdAt must be between before and after");
        assertTrue(saved.getUpdatedAt() >= before && saved.getUpdatedAt() <= after + 1000,
                "updatedAt must be between before and after");
    }

    @Test
    void save_updatesExistingCartItem(){
        CartItem existingCartItem = CartItem.builder()
                .id(1L)
                .user(user)
                .product(product)
                .quantity(2) // déjà 2 dans le panier
                .createdAt(System.currentTimeMillis() - 1000)
                .updatedAt(System.currentTimeMillis() - 1000)
                .build();

        // Mocks des services
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(productService.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductId(1L, 1L))
                .thenReturn(Optional.of(existingCartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execution
        CartItem saved = cartItemService.save(cartItemDTO, 1L);

        // Assertions
        assertNotNull(saved.getId(), "id should not be null");
        assertEquals(1L, saved.getId(), "id should be 1");
        assertEquals("X", saved.getProduct().getName(), "name should be X");
        assertEquals("test@test.fr", saved.getUser().getEmail(), "email should be test@test.fr");
        assertEquals(4, saved.getQuantity(), "quantity should be 4"); // 2 + 2

        assertTrue(saved.getUpdatedAt() > saved.getCreatedAt(), "updated should be greater than created");
    }

    @Test
    void save_throwsUserNotFoundException_whenUserDoesNotExist() {
        when(userService.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> cartItemService.save(cartItemDTO, 1L));

    }

    @Test
    void save_throwsProductNotFoundException_whenProductDoesNotExist() {
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(productService.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> cartItemService.save(cartItemDTO, 1L));
    }

    @Test
    void delete_callsRepositoryDeleteById() {
        when(cartItemRepository.existsByIdAndUserId(itemId, userId)).thenReturn(true);

        cartItemService.delete(1L, 1L);

        verify(cartItemRepository).deleteById(1L);
    }

    @Test
    void delete_throwsException_whenItemDoesNotExist() {
        when(cartItemRepository.existsByIdAndUserId(itemId, userId)).thenReturn(false);

        assertThrows(CartItemNotFoundException.class, () -> cartItemService.delete(itemId, userId));
        verify(cartItemRepository, never()).deleteById(any());
    }

    @Test
    void deleteAll_callsRepositoryDeleteAll() {
        cartItemService.deleteAll();
        verify(cartItemRepository).deleteAll();
    }

    @Test
    void getCart_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);

        CartItem item = CartItem.builder().id(1L).build();
        Page<CartItem> page = new PageImpl<>(List.of(item));

        when(cartItemRepository.findByUserId(pageable, userId)).thenReturn(page);

        Page<CartItemDTO> result = cartItemService.getCart(pageable, userId);

        assertEquals(1, result.getTotalElements());
        assertEquals(item.getId(), result.getContent().get(0).getId());
    }

    @Test
    void getCartItemById_returnsMappedDto_whenItemExists() {


        CartItem item = CartItem.builder().id(itemId).build();
        when(cartItemRepository.findByIdAndUserId(itemId, userId)).thenReturn(Optional.of(item));

        Optional<CartItemDTO> dto = cartItemService.getCartItemById(itemId, userId);

        assertTrue(dto.isPresent());
        assertEquals(itemId, dto.get().getId());
    }

    @Test
    void getCartItemById_returnsEmpty_whenItemDoesNotExist() {
        when(cartItemRepository.findByIdAndUserId(itemId, userId)).thenReturn(Optional.empty());

        Optional<CartItemDTO> dto = cartItemService.getCartItemById(itemId, userId);

        assertTrue(dto.isEmpty());
    }

}
