package com.producttrial.back.service.kafka;

import com.producttrial.back.config.KafkaTopics;
import com.producttrial.back.dto.kafka.product.*;
import com.producttrial.back.dto.kafka.user.UserRegisteredEvent;
import com.producttrial.back.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // ==================== PRODUCT LIFECYCLE ====================

    public void sendProductCreated(Product product) {
        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .productId(String.valueOf(product.getId()))
                .name(product.getName())
                .code(product.getCode())
                .price(product.getPrice())
                .category(product.getCategory())
                .timestamp(System.currentTimeMillis())
                .build();
        
        kafkaTemplate.send(KafkaTopics.PRODUCT_LIFECYCLE, String.valueOf(product.getId()), event);
        log.info("Sent ProductCreatedEvent for productId={}", product.getId());
    }

    public void sendProductUpdated(Long productId, Product oldProduct, Product newProduct) {
        ProductUpdatedEvent event = ProductUpdatedEvent.builder()
                .productId(String.valueOf(productId))
                .changedFields(new String[]{"name", "price"})
                .oldValues(new String[]{oldProduct.getName(), String.valueOf(oldProduct.getPrice())})
                .newValues(new String[]{newProduct.getName(), String.valueOf(newProduct.getPrice())})
                .timestamp(System.currentTimeMillis())
                .build();
        
        kafkaTemplate.send(KafkaTopics.PRODUCT_LIFECYCLE, String.valueOf(productId), event);
        log.info("Sent ProductUpdatedEvent for productId={}", productId);
    }

    public void sendProductDeleted(Long productId, String code) {
        ProductDeletedEvent event = ProductDeletedEvent.builder()
                .productId(String.valueOf(productId))
                .code(code)
                .timestamp(System.currentTimeMillis())
                .build();
        
        kafkaTemplate.send(KafkaTopics.PRODUCT_LIFECYCLE, String.valueOf(productId), event);
        log.info("Sent ProductDeletedEvent for productId={}", productId);
    }

    // ==================== PRODUCT ANALYTICS ====================

    public void sendProductViewed(Long productId, Long userId) {
        ProductViewedEvent event = ProductViewedEvent.builder()
                .productId(String.valueOf(productId))
                .userId(userId != null ? String.valueOf(userId) : null)
                .timestamp(System.currentTimeMillis())
                .build();
        
        kafkaTemplate.send(KafkaTopics.PRODUCT_ANALYTICS, String.valueOf(productId), event);
        log.info("Sent ProductViewedEvent for productId={}", productId);
    }

    // ==================== USER EVENTS ====================

    public void sendUserRegistered(Long userId, String email) {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(String.valueOf(userId))
                .email(email)
                .timestamp(System.currentTimeMillis())
                .build();
        
        kafkaTemplate.send(KafkaTopics.USER_EVENTS, String.valueOf(userId), event);
        log.info("Sent UserRegisteredEvent for userId={}", userId);
    }
}