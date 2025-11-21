package com.producttrial.back.dto;

import com.producttrial.back.enums.InventoryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    @NotNull(message = "Code is required")
    private String code;
    @NotNull(message = "Name is required")
    private String name;
    private String description;
    private String image;
    private String category;
    @NotNull(message = "Price is required")
    private Double price;
    private Integer quantity;
    private String internalReference;
    private Long shellId;
    private InventoryStatus inventoryStatus;
    private Double rating;
}
