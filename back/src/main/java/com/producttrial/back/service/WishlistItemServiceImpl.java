package com.producttrial.back.service;

import com.producttrial.back.dto.WishlistItemDTO;
import com.producttrial.back.entity.Product;
import com.producttrial.back.entity.User;
import com.producttrial.back.entity.WishlistItem;
import com.producttrial.back.exception.ProductNotFoundException;
import com.producttrial.back.exception.UserNotFoundException;
import com.producttrial.back.exception.WishlistItemNotFoundException;
import com.producttrial.back.mapper.WishlistItemMapper;
import com.producttrial.back.repository.WishlistItemRepository;
import com.producttrial.back.service.iservice.IProductService;
import com.producttrial.back.service.iservice.IUserService;
import com.producttrial.back.service.iservice.IWishlistItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistItemServiceImpl implements IWishlistItemService {
    private final WishlistItemRepository wishlistItemRepository;
    private final IUserService userService;
    private final IProductService productService;

    // Méthode de wishlistItem

    @Override
    public List<WishlistItem> findByUserId(Long userId) {
        log.debug("Fetching all wishlist items of the user with id {}", userId);
        return wishlistItemRepository.findByUserId(userId);
    }

    @Override
    public WishlistItem save(WishlistItemDTO dto, Long userId) {
        User user = userService.findById(userId).orElseThrow(() -> {
            log.warn("User not found for update id={}", userId);
            return new UserNotFoundException(userId);
        });
        Long productId = dto.getProductId();
        Product product = productService.findById(productId).orElseThrow(() -> {
            log.warn("Product not found for update id={}", productId);
            return new ProductNotFoundException(productId);
        });

        long now = System.currentTimeMillis();
        Optional<WishlistItem> existingWishlistItem = wishlistItemRepository.findByUserIdAndProductId(userId, productId);
        if (existingWishlistItem.isPresent()) {
            throw new IllegalArgumentException("Product already in wishlist");
        }

        WishlistItem wishlistItem = WishlistItem.builder()
                .user(user)
                .product(product)
                .createdAt(now)
                .updatedAt(now)
                .build();
        WishlistItem saved = wishlistItemRepository.save(wishlistItem);
        log.debug("Saved wishlist item with id {}", saved.getId());
        return saved;
    }

    @Override
    public void delete(Long id, Long userId) {
        try {
            if (!wishlistItemRepository.existsByIdAndUserId(id, userId)) {
                log.warn("Attempted to delete non-existing wishlist item id={}", id);
                throw new WishlistItemNotFoundException(id);
            }
            wishlistItemRepository.deleteById(id);
            log.info("Deleted wishlist item id={}", id);
        } catch (Exception e) {
            log.error("Error deleting wishlist id={}", id, e);
            throw e;
        }
    }

    @Override
    public void deleteAll() {
        wishlistItemRepository.deleteAll();
        log.info("Deleted all wishlist items");
    }

    // Méthode de wishlistItemDTO

    @Override
    public Page<WishlistItemDTO> getWishlist(Pageable pageable, Long userId) {
        return wishlistItemRepository.findByUserId(pageable, userId).map(WishlistItemMapper::toDto);
    }

    @Override
    public Optional<WishlistItemDTO> getWishlistItemById(Long id, Long userId) {
        return wishlistItemRepository.findByIdAndUserId(id, userId).map(WishlistItemMapper::toDto);
    }
}
