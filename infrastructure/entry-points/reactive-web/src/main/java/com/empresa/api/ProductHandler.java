package com.empresa.api;

import com.empresa.model.Product;
import com.empresa.usecase.ProductUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * RONDA 4: Logging estructurado
 * 
 * Recibe peticiones HTTP y delega al caso de uso
 * Transforma respuestas del dominio a respuestas HTTP
 */
public class ProductHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductHandler.class);
    
    private final ProductUseCase productUseCase;
    private final Validator validator;
    
    public ProductHandler(ProductUseCase productUseCase, Validator validator) {
        this.productUseCase = productUseCase;
        this.validator = validator;
    }
    
    public Mono<ServerResponse> getAllProducts(ServerRequest request) {
        logger.info("GET /products - Obteniendo todos los productos");
        return ServerResponse.ok()
                .body(productUseCase.getAllProducts(), Product.class)
                .doOnSuccess(r -> logger.debug("Productos obtenidos exitosamente"));
    }
    
    public Mono<ServerResponse> getProductById(ServerRequest request) {
        String id = request.pathVariable("id");
        return productUseCase.getProductById(id)
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
    
    public Mono<ServerResponse> createProduct(ServerRequest request) {
        logger.info("POST /products - Creando nuevo producto");
        return request.bodyToMono(Product.class)
                .flatMap(product -> {
                    logger.debug("Validando producto: {}", product.getName());
                    // Validar el producto
                    Set<ConstraintViolation<Product>> violations = validator.validate(product);
                    if (!violations.isEmpty()) {
                        String errors = violations.stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.joining(", "));
                        logger.warn("Validación fallida: {}", errors);
                        return Mono.error(new IllegalArgumentException(errors));
                    }
                    return Mono.just(product);
                })
                .flatMap(productUseCase::createProduct)
                .flatMap(product -> {
                    logger.info("Producto creado exitosamente: {} (ID: {})", product.getName(), product.getId());
                    return ServerResponse.status(201).bodyValue(product);
                })
                .onErrorResume(e -> {
                    logger.error("Error al crear producto: {}", e.getMessage());
                    return ServerResponse.badRequest().bodyValue(e.getMessage());
                });
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
