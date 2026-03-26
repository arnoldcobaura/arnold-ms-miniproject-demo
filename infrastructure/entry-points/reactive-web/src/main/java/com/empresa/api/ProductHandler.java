package com.empresa.api;

import com.empresa.model.Product;
import com.empresa.usecase.ProductUseCase;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * PASO 8a: HANDLER - MANEJA LAS PETICIONES HTTP
 * 
 * RONDA 3: Validación con Bean Validation
 * 
 * Recibe peticiones HTTP y delega al caso de uso
 * Transforma respuestas del dominio a respuestas HTTP
 */
public class ProductHandler {
    
    private final ProductUseCase productUseCase;
    private final Validator validator;
    
    public ProductHandler(ProductUseCase productUseCase, Validator validator) {
        this.productUseCase = productUseCase;
        this.validator = validator;
    }
    
    public Mono<ServerResponse> getAllProducts(ServerRequest request) {
        return ServerResponse.ok()
                .body(productUseCase.getAllProducts(), Product.class);
    }
    
    public Mono<ServerResponse> getProductById(ServerRequest request) {
        String id = request.pathVariable("id");
        return productUseCase.getProductById(id)
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
    
    public Mono<ServerResponse> createProduct(ServerRequest request) {
        return request.bodyToMono(Product.class)
                .flatMap(product -> {
                    // Validar el producto
                    Set<ConstraintViolation<Product>> violations = validator.validate(product);
                    if (!violations.isEmpty()) {
                        String errors = violations.stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.joining(", "));
                        return Mono.error(new IllegalArgumentException(errors));
                    }
                    return Mono.just(product);
                })
                .flatMap(productUseCase::createProduct)
                .flatMap(product -> ServerResponse.status(201).bodyValue(product))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
    
    public Mono<ServerResponse> updateProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(Product.class)
                .flatMap(product -> {
                    // Validar el producto
                    Set<ConstraintViolation<Product>> violations = validator.validate(product);
                    if (!violations.isEmpty()) {
                        String errors = violations.stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.joining(", "));
                        return Mono.error(new IllegalArgumentException(errors));
                    }
                    return Mono.just(product);
                })
                .flatMap(product -> productUseCase.updateProduct(id, product))
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
    
    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        return productUseCase.deleteProduct(id)
                .then(ServerResponse.noContent().build())
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
    
    public Mono<ServerResponse> getExpensiveProducts(ServerRequest request) {
        return ServerResponse.ok()
                .body(productUseCase.getExpensiveProducts(), Product.class);
    }
}
