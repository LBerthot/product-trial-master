package com.producttrial.back.service;

import com.producttrial.back.dto.CartItemDTO;
import com.producttrial.back.entity.CartItem;
import com.producttrial.back.entity.Product;
import com.producttrial.back.entity.User;
import com.producttrial.back.exception.CartItemNotFoundException;
import com.producttrial.back.exception.ProductNotFoundException;
import com.producttrial.back.exception.UserNotFoundException;
import com.producttrial.back.mapper.CartItemMapper;
import com.producttrial.back.repository.CartItemRepository;
import jakarta.persistence.EntityManager;
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
public class CartItemServiceImpl implements ICartItemService{
    private final CartItemRepository cartItemRepository;
    private final IUserService userService;
    private final IProductService productService;
    private final EntityManager entityManager;

    // Méthode de CartItem

    @Override
    public List<CartItem> findByUserId(Long userId) {
        log.debug("Fetching all cart items of the user with id {}", userId);
        return cartItemRepository.findByUserId(userId);
    }

    @Override
    public CartItem save(CartItemDTO dto, Long userId) {
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
        Optional<CartItem> existingCartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (existingCartItem.isPresent()) {
            CartItem existing = existingCartItem.get();
            existing.setQuantity(existing.getQuantity() + dto.getQuantity());
            existing.setUpdatedAt(now);
            return cartItemRepository.save(existing);
        }

        CartItem newCartItem = CartItem.builder()
                .user(user)
                .product(product)
                .quantity(dto.getQuantity())
                .createdAt(now)
                .updatedAt(now)
                .build();
        CartItem saved = cartItemRepository.save(newCartItem);
        log.debug("Saved cart item with id {}", saved.getId());
        return saved;
    }

    @Override
    public void delete(Long id, Long userId) {
        try {
            if (!cartItemRepository.existsByIdAndUserId(id, userId)) {
                log.warn("Attempted to delete non-existing cart item id={}", id);
                throw new CartItemNotFoundException(id);
            }
            cartItemRepository.deleteById(id);
            log.info("Deleted cart item id={}", id);
        } catch (Exception e) {
            log.error("Error deleting product id={}", id, e);
            throw e;
        }
    }

    @Override
    public void deleteAll() {
        cartItemRepository.deleteAll();
        log.info("Deleted all cart items");
    }

    // Méthode de CartItemDTO

    @Override
    public Page<CartItemDTO> getCart(Pageable pageable, Long userId) {
        return cartItemRepository.findByUserId(pageable, userId).map(CartItemMapper::toDto);
    }

    @Override
    public Optional<CartItemDTO> getCartItemById(Long id, Long userId) {
        return cartItemRepository.findByIdAndUserId(id, userId).map(CartItemMapper::toDto);
    }
}
