package com.producttrial.back.dto.kafka.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdatedEvent {
    private String productId;
    private String[] changedFields;
    private String[] oldValues;
    private String[] newValues;
    private Long timestamp;
}