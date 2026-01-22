package com.producttrial.back.service.kafka;

import com.producttrial.back.config.KafkaTopics;
import com.producttrial.back.dto.kafka.product.*;
import com.producttrial.back.dto.kafka.user.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = KafkaTopics.PRODUCT_LIFECYCLE, groupId = "${spring.kafka.consumer.group-id}")
    public void listenProductLifecycle(Object event) {
        switch (event) {
            case ProductCreatedEvent created ->
                    log.info("Product created - productId={}, name={}", created.getProductId(), created.getName());
            case ProductUpdatedEvent updated ->
                    log.info("Product updated - productId={}, fields={}", updated.getProductId(), updated.getChangedFields());
            case ProductDeletedEvent deleted ->
                    log.info("Product deleted - productId={}, code={}", deleted.getProductId(), deleted.getCode());
            default -> log.warn("Unknown product lifecycle event: {}", event.getClass().getSimpleName());
        }
    }

    @KafkaListener(topics = KafkaTopics.PRODUCT_ANALYTICS, groupId = "${spring.kafka.consumer.group-id}")
    public void listenProductAnalytics(ProductViewedEvent event) {
        log.info("Product viewed - productId={}, userId={}", event.getProductId(), event.getUserId());
    }

    @KafkaListener(topics = KafkaTopics.USER_EVENTS, groupId = "${spring.kafka.consumer.group-id}")
    public void listenUserEvents(UserRegisteredEvent event) {
        log.info("User registered - userId={}, email={}", event.getUserId(), event.getEmail());
    }
}