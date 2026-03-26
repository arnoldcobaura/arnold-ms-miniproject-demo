package com.empresa.api;

import com.empresa.api.doc.ProductOpenApi;
import com.empresa.model.Product;
import com.empresa.model.gateway.ProductGateway;
import com.empresa.usecase.ProductUseCase;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import jakarta.validation.Validator;

/**
 * PASO 8b: ROUTER - DEFINE LAS RUTAS HTTP
 * 
 * RONDA 3: Configuración de validación
 * RONDA 4: Documentación OpenAPI/Swagger con SpringdocRouteBuilder
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
        return SpringdocRouteBuilder.route()
                .GET("/products", handler::getAllProducts, ProductOpenApi.getAllProductsOpenAPI())
                .GET("/products/{id}", handler::getProductById, ProductOpenApi.getProductByIdOpenAPI())
                .GET("/products/expensive", handler::getExpensiveProducts, ProductOpenApi.getExpensiveProductsOpenAPI())
                .POST("/products", handler::createProduct, ProductOpenApi.createProductOpenAPI())
                .PUT("/products/{id}", handler::updateProduct, ProductOpenApi.updateProductOpenAPI())
                .DELETE("/products/{id}", handler::deleteProduct, ProductOpenApi.deleteProductOpenAPI())
                .build();
    }
}
