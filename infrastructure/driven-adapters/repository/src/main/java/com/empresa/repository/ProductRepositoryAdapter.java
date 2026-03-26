package com.empresa.repository;

import com.empresa.model.Product;
import com.empresa.model.gateway.ProductGateway;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * PASO 7: ADAPTADOR DE REPOSITORIO - IMPLEMENTACIÓN DEL PUERTO
 * 
 * Implementa ProductGateway (puerto del dominio)
 * RONDA 2: Ahora usa MongoDB en lugar de BD en memoria
 * 
 * Delega todas las operaciones a ProductMongoRepository
 * Spring Data genera automáticamente las implementaciones
 */
@Component
public class ProductRepositoryAdapter implements ProductGateway {
    
    private final ProductMongoRepository mongoRepository;
    
    public ProductRepositoryAdapter(ProductMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }
    
    @Override
    public Mono<Product> findById(String id) {
        return mongoRepository.findById(id);
    }
    
    @Override
    public Flux<Product> findAll() {
        return mongoRepository.findAll();
    }
    
    @Override
    public Mono<Product> save(Product product) {
        // Si no tiene ID, generar uno
        if (product.getId() == null || product.getId().isEmpty()) {
            product.setId(UUID.randomUUID().toString());
        }
        return mongoRepository.save(product);
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepository.deleteById(id);
    }
    
    @Override
    public Mono<Boolean> existsById(String id) {
        return mongoRepository.existsById(id);
    }
}
