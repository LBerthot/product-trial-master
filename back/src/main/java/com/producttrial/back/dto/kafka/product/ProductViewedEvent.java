package com.producttrial.back.dto.kafka.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductViewedEvent {
    private String productId;
    private String userId;
    private Long timestamp;
}