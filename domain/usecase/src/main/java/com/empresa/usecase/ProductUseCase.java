package com.empresa.usecase;

import com.empresa.model.Product;
import com.empresa.model.gateway.ProductGateway;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * PASO 6: CASO DE USO - LÓGICA DE NEGOCIO
 * 
 * Orquesta las operaciones del dominio
 * Usa ProductGateway (puerto) sin saber cómo está implementado
 * NO tiene dependencias de Spring ni frameworks
 */
public class ProductUseCase {
    
    private final ProductGateway productGateway;
    
    public ProductUseCase(ProductGateway productGateway) {
        this.productGateway = productGateway;
    }
    
    public Flux<Product> getAllProducts() {
        return productGateway.findAll();
    }
    
    public Mono<Product> getProductById(String id) {
        return productGateway.findById(id);
    }
    
    public Mono<Product> createProduct(Product product) {
        return Mono.just(product)
                .filter(Product::isValid)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Producto inválido")))
                .flatMap(productGateway::save);
    }
    
    public Mono<Product> updateProduct(String id, Product product) {
        return productGateway.existsById(id)
                .filter(exists -> exists)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Producto no encontrado")))
                .then(Mono.just(product))
                .doOnNext(p -> p.setId(id))
                .filter(Product::isValid)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Producto inválido")))
                .flatMap(productGateway::save);
    }
    
    public Mono<Void> deleteProduct(String id) {
        return productGateway.existsById(id)
                .filter(exists -> exists)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Producto no encontrado")))
                .then(productGateway.deleteById(id));
    }
    
    public Flux<Product> getExpensiveProducts() {
        return productGateway.findAll()
                .filter(Product::isExpensive);
    }
}
