package com.empresa.api;

import com.empresa.model.Product;
import com.empresa.usecase.ProductUseCase;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * PASO 8a: HANDLER - MANEJA LAS PETICIONES HTTP
 * 
 * Recibe peticiones HTTP y delega al caso de uso
 * Transforma respuestas del dominio a respuestas HTTP
 */
public class ProductHandler {
    
    private final ProductUseCase productUseCase;
    
    public ProductHandler(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
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
                .flatMap(productUseCase::createProduct)
                .flatMap(product -> ServerResponse.status(201).bodyValue(product))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
    
    public Mono<ServerResponse> updateProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(Product.class)
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
