package com.producttrial.back.service;

import com.producttrial.back.dto.WishlistItemDTO;
import com.producttrial.back.entity.Product;
import com.producttrial.back.entity.User;
import com.producttrial.back.entity.WishlistItem;
import com.producttrial.back.exception.ProductNotFoundException;
import com.producttrial.back.exception.UserNotFoundException;
import com.producttrial.back.exception.WishlistItemNotFoundException;
import com.producttrial.back.repository.WishlistItemRepository;
import com.producttrial.back.service.iservice.IProductService;
import com.producttrial.back.service.iservice.IUserService;
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
class WishlistItemServiceTest {
    @Mock
    private WishlistItemRepository wishlistItemRepository;
    @Mock
    private IUserService userService;
    @Mock
    private IProductService productService;

    @InjectMocks
    private WishlistItemServiceImpl wishlistItemService;

    private User user;
    private Product product;
    private WishlistItemDTO wishlistItemDTO;
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

        wishlistItemDTO = WishlistItemDTO.builder()
                .productId(1L)
                .build();
    }
    @Test
    void findByUserId_returnsListFromRepository() {
        when(wishlistItemRepository.findByUserId(1L)).thenReturn(List.of(new WishlistItem(), new WishlistItem()));

        List<WishlistItem> result = wishlistItemRepository.findByUserId(1L);
        assertEquals(2, result.size(), "list should contain 2 element");
    }

    @Test
    void save_setsTimestampsAndReturnsSavedWishlistItem() {
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(productService.findById(1L)).thenReturn(Optional.of(product));
        when(wishlistItemRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
        when(wishlistItemRepository.save(any(WishlistItem.class))).thenAnswer(invocation -> {
            WishlistItem w = invocation.getArgument(0);
            w.setId(1L);
            return w;
        });


        // Pour vérifier que les timestamps sont dans le bon interval
        long before = System.currentTimeMillis();
        WishlistItem saved = wishlistItemService.save(wishlistItemDTO, 1L);
        long after = System.currentTimeMillis();

        assertNotNull(saved.getId(), "id should not be null");
        assertEquals(1L, saved.getId(), "id should be 1");
        assertEquals("X", saved.getProduct().getName(), "name should be X");
        assertEquals("test@test.fr", saved.getUser().getEmail(),"email should be test@test.fr");

        assertNotNull(saved.getCreatedAt(), "createdAt should not be null");
        assertNotNull(saved.getUpdatedAt(), "updatedAt should not be null");
        assertTrue(saved.getCreatedAt() >= before && saved.getCreatedAt() <= after + 1000,
                "createdAt must be between before and after");
        assertTrue(saved.getUpdatedAt() >= before && saved.getUpdatedAt() <= after + 1000,
                "updatedAt must be between before and after");
    }

    @Test
    void save_throwsException_whenItemAlreadyExists() {
        WishlistItem existingWishlistItem = WishlistItem.builder()
                .id(1L)
                .user(user)
                .product(product)
                .createdAt(System.currentTimeMillis())
                .updatedAt(System.currentTimeMillis())
                .build();

        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(productService.findById(1L)).thenReturn(Optional.of(product));
        when(wishlistItemRepository.findByUserIdAndProductId(1L, 1L))
                .thenReturn(Optional.of(existingWishlistItem));

        assertThrows(IllegalArgumentException.class, () -> wishlistItemService.save(wishlistItemDTO, 1L));
    }


    @Test
    void save_throwsUserNotFoundException_whenUserDoesNotExist() {
        when(userService.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> wishlistItemService.save(wishlistItemDTO, 1L));

    }

    @Test
    void save_throwsProductNotFoundException_whenProductDoesNotExist() {
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(productService.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> wishlistItemService.save(wishlistItemDTO, 1L));
    }

    @Test
    void delete_callsRepositoryDeleteById() {
        when(wishlistItemRepository.existsByIdAndUserId(itemId, userId)).thenReturn(true);

        wishlistItemService.delete(1L, 1L);

        verify(wishlistItemRepository).deleteById(1L);
    }

    @Test
    void delete_throwsException_whenItemDoesNotExist() {
        when(wishlistItemRepository.existsByIdAndUserId(itemId, userId)).thenReturn(false);

        assertThrows(WishlistItemNotFoundException.class, () -> wishlistItemService.delete(itemId, userId));
        verify(wishlistItemRepository, never()).deleteById(any());
    }

    @Test
    void deleteAll_callsRepositoryDeleteAll() {
        wishlistItemService.deleteAll();
        verify(wishlistItemRepository).deleteAll();
    }

    @Test
    void getWishlist_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);

        WishlistItem wishlistItem = WishlistItem.builder().id(1L).build();
        Page<WishlistItem> page = new PageImpl<>(List.of(wishlistItem));

        when(wishlistItemRepository.findByUserId(pageable, userId)).thenReturn(page);

        Page<WishlistItemDTO> result = wishlistItemService.getWishlist(pageable, userId);

        assertEquals(1, result.getTotalElements());
        assertEquals(wishlistItem.getId(), result.getContent().getFirst().getId());
    }

    @Test
    void getWishlistItemById_returnsMappedDto_whenItemExists() {
        WishlistItem item = WishlistItem.builder().id(itemId).build();
        when(wishlistItemRepository.findByIdAndUserId(itemId, userId)).thenReturn(Optional.of(item));

        Optional<WishlistItemDTO> dto = wishlistItemService.getWishlistItemById(itemId, userId);

        assertTrue(dto.isPresent());
        assertEquals(itemId, dto.get().getId());
    }

    @Test
    void getWishlistItemById_returnsEmpty_whenItemDoesNotExist() {
        when(wishlistItemRepository.findByIdAndUserId(itemId, userId)).thenReturn(Optional.empty());

        Optional<WishlistItemDTO> dto = wishlistItemService.getWishlistItemById(itemId, userId);

        assertTrue(dto.isEmpty());
    }
}
