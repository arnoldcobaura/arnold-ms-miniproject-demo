package com.empresa.repository;

import com.empresa.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * RONDA 2: REPOSITORIO MONGODB - SPRING DATA REACTIVE
 * 
 * Extiende ReactiveMongoRepository para obtener operaciones CRUD reactivas
 * Spring Data genera automáticamente la implementación
 * 
 * Métodos heredados:
 * - findById(String id): Mono<Product>
 * - findAll(): Flux<Product>
 * - save(Product): Mono<Product>
 * - deleteById(String id): Mono<Void>
 * - existsById(String id): Mono<Boolean>
 */
@Repository
public interface ProductMongoRepository extends ReactiveMongoRepository<Product, String> {
    // Spring Data genera automáticamente todas las operaciones CRUD
    // No necesitamos implementar nada
}
