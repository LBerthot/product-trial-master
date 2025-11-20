package com.producttrial.back.mapper;

import com.producttrial.back.dto.ProductDTO;
import com.producttrial.back.entity.Product;

public class ProductMapper {
    private ProductMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ProductDTO toDto(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .description(product.getDescription())
                .image(product.getImage())
                .category(product.getCategory())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .internalReference(product.getInternalReference())
                .shellId(product.getShellId())
                .inventoryStatus(product.getInventoryStatus())
                .rating(product.getRating())
                .build();
    }
}
