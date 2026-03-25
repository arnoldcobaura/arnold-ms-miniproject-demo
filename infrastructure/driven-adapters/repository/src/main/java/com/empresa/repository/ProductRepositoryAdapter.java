package com.empresa.repository;

import com.empresa.model.Product;
import com.empresa.model.gateway.ProductGateway;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PASO 7: ADAPTADOR DE REPOSITORIO - IMPLEMENTACIÓN DEL PUERTO
 * 
 * Implementa ProductGateway (puerto del dominio)
 * En este caso, usa una BD en memoria (Map)
 * En producción, sería una BD real (MongoDB, PostgreSQL, etc.)
 */
@Component
public class ProductRepositoryAdapter implements ProductGateway {
    
    private final Map<String, Product> database = new ConcurrentHashMap<>();
    
    @Override
    public Mono<Product> findById(String id) {
        return Mono.fromCallable(() -> database.get(id))
                .flatMap(product -> product != null ? Mono.just(product) : Mono.empty());
    }
    
    @Override
    public Flux<Product> findAll() {
        return Flux.fromIterable(database.values());
    }
    
    @Override
    public Mono<Product> save(Product product) {
        return Mono.fromCallable(() -> {
            if (product.getId() == null || product.getId().isEmpty()) {
                product.setId(UUID.randomUUID().toString());
            }
            database.put(product.getId(), product);
            return product;
        });
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.fromRunnable(() -> database.remove(id));
    }
    
    @Override
    public Mono<Boolean> existsById(String id) {
        return Mono.just(database.containsKey(id));
    }
}
