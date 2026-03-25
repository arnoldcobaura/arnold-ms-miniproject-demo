package com.empresa.model.gateway;

import com.empresa.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * PASO 5: PUERTO (Gateway/Interface) DEL DOMINIO
 * 
 * Este es un PUERTO de salida - define CÓMO el dominio se comunica con el exterior
 * 
 * Principios de Clean Architecture:
 * - El dominio DEFINE el contrato (interface)
 * - La infraestructura IMPLEMENTA el contrato
 * - El dominio NO sabe CÓMO se implementa (Redis, BD, archivo, etc.)
 */
public interface ProductGateway {
    
    /**
     * Buscar un producto por su ID
     * @param id Identificador único del producto
     * @return Mono<Product> - Stream reactivo con 0 o 1 productos
     */
    Mono<Product> findById(String id);
    
    /**
     * Obtener todos los productos
     * @return Flux<Product> - Stream reactivo con 0 a N productos
     */
    Flux<Product> findAll();
    
    /**
     * Guardar un producto (crear o actualizar)
     * @param product Producto a guardar
     * @return Mono<Product> - Stream reactivo con el producto guardado
     */
    Mono<Product> save(Product product);
    
    /**
     * Eliminar un producto por ID
     * @param id Identificador del producto a eliminar
     * @return Mono<Void> - Stream reactivo que completa cuando se elimina
     */
    Mono<Void> deleteById(String id);
    
    /**
     * Verificar si existe un producto
     * @param id Identificador del producto
     * @return Mono<Boolean> - Stream reactivo con true/false
     */
    Mono<Boolean> existsById(String id);
}
