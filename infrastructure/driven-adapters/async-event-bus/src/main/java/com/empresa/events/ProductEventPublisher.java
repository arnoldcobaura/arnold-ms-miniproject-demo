package com.empresa.events;

import com.empresa.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * RONDA 5a: EVENT PUBLISHER - PUBLICA EVENTOS A RABBITMQ
 * 
 * Responsable de publicar eventos de productos a través de RabbitMQ
 * Usa Spring AMQP RabbitTemplate para manejo de mensajes
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String EXCHANGE = "product.events";
    
    public Mono<Void> publishProductCreated(Product product) {
        log.info("Publicando evento ProductCreated para: {}", product.getName());
        ProductCreatedEvent event = new ProductCreatedEvent(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getCategory()
        );
        return publishEvent("product.created", event);
    }
    
    public Mono<Void> publishProductUpdated(Product product) {
        log.info("Publicando evento ProductUpdated para: {}", product.getName());
        ProductUpdatedEvent event = new ProductUpdatedEvent(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getCategory()
        );
        return publishEvent("product.updated", event);
    }
    
    public Mono<Void> publishProductDeleted(String productId) {
        log.info("Publicando evento ProductDeleted para ID: {}", productId);
        ProductDeletedEvent event = new ProductDeletedEvent(productId);
        return publishEvent("product.deleted", event);
    }
    
    private Mono<Void> publishEvent(String routingKey, Object event) {
        return Mono.fromRunnable(() -> {
            try {
                String message = objectMapper.writeValueAsString(event);
                rabbitTemplate.convertAndSend(EXCHANGE, routingKey, message);
                log.debug("Evento publicado con routing key: {}", routingKey);
            } catch (Exception e) {
                log.error("Error publicando evento: {}", e.getMessage(), e);
                throw new RuntimeException("Error publicando evento", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
