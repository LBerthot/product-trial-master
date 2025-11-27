package com.producttrial.back.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long id;
    @NotNull(message = "Product id is required")
    private Long productId;
    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
