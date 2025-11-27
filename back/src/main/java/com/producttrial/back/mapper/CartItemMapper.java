package com.producttrial.back.mapper;

import com.producttrial.back.dto.CartItemDTO;
import com.producttrial.back.entity.CartItem;

public class CartItemMapper {
    private CartItemMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static CartItemDTO toDto(CartItem cartItem) {
        return CartItemDTO.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct() != null ? cartItem.getProduct().getId() : null)
                .quantity(cartItem.getQuantity())
                .build();
    }
}
