package com.producttrial.back.repository;

import com.producttrial.back.entity.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    Page<CartItem> findByUserId(Pageable pageable, Long userId);
    Optional<CartItem> findByIdAndUserId(Long id, Long userId);
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);
    boolean existsByIdAndUserId(Long id, Long userId);
}
