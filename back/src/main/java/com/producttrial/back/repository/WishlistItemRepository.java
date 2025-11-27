package com.producttrial.back.repository;

import com.producttrial.back.entity.WishlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUserId(Long userId);
    Page<WishlistItem> findByUserId(Pageable pageable, Long userId);
    Optional<WishlistItem> findByIdAndUserId(Long id, Long userId);
    Optional<WishlistItem> findByUserIdAndProductId(Long userId, Long productId);
    boolean existsByIdAndUserId(Long id, Long userId);
}
