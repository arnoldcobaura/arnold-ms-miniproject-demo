package com.empresa.api;

import com.empresa.model.gateway.ProductGateway;
import com.empresa.usecase.ProductUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import jakarta.validation.Validator;

/**
 * PASO 8b: ROUTER - DEFINE LAS RUTAS HTTP
 * 
 * RONDA 3: Configuración de validación
 * 
 * Mapea URLs a handlers
 * Configura los beans necesarios (inyección de dependencias)
 */
@Configuration
public class RouterRest {
    
    @Bean
    public ProductUseCase productUseCase(ProductGateway productGateway) {
        return new ProductUseCase(productGateway);
    }
    
    @Bean
    public ProductHandler productHandler(ProductUseCase productUseCase, Validator validator) {
        return new ProductHandler(productUseCase, validator);
    }
    
    @Bean
    public RouterFunction<ServerResponse> routes(ProductHandler handler) {
        return RouterFunctions.route()
                .GET("/products", handler::getAllProducts)
                .GET("/products/expensive", handler::getExpensiveProducts)
                .GET("/products/{id}", handler::getProductById)
                .POST("/products", handler::createProduct)
                .PUT("/products/{id}", handler::updateProduct)
                .DELETE("/products/{id}", handler::deleteProduct)
                .build();
    }
}
