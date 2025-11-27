package com.producttrial.back.mapper;

import com.producttrial.back.dto.WishlistItemDTO;
import com.producttrial.back.entity.WishlistItem;

public class WishlistItemMapper {
    private WishlistItemMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static WishlistItemDTO toDto(WishlistItem wishlistItem) {
        return WishlistItemDTO.builder()
                .id(wishlistItem.getId())
                .productId(wishlistItem.getProduct() != null ? wishlistItem.getProduct().getId() : null)
                .build();
    }
}
