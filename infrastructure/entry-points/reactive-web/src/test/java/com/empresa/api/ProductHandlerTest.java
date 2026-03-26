package com.empresa.api;

import com.empresa.cache.ProductCacheService;
import com.empresa.events.ProductEventPublisher;
import com.empresa.model.Product;
import com.empresa.usecase.ProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import jakarta.validation.Validator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * RONDA 4b: TESTS UNITARIOS CON MOCKITO Y REACTOR TEST
 * RONDA 5a: Tests para eventos asíncronos
 * RONDA 5c: Tests para caching
 * 
 * Pruebas para ProductHandler usando StepVerifier
 */
@DisplayName("ProductHandler Tests")
class ProductHandlerTest {
    
    @Mock
    private ProductUseCase productUseCase;
    
    @Mock
    private Validator validator;
    
    @Mock
    private ProductEventPublisher eventPublisher;
    
    @Mock
    private ProductCacheService cacheService;
    
    private ProductHandler productHandler;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(eventPublisher.publishProductCreated(any())).thenReturn(Mono.empty());
        when(eventPublisher.publishProductUpdated(any())).thenReturn(Mono.empty());
        when(eventPublisher.publishProductDeleted(anyString())).thenReturn(Mono.empty());
        when(cacheService.cacheProduct(any())).thenReturn(Mono.empty());
        when(cacheService.invalidateProductCache(anyString())).thenReturn(Mono.empty());
        productHandler = new ProductHandler(productUseCase, validator, eventPublisher, cacheService);
    }
    
    @Test
    @DisplayName("Debe obtener todos los productos")
    void testGetAllProducts() {
        // Arrange
        Product product1 = Product.builder()
                .id("1")
                .name("Laptop")
                .price(1200.0)
                .category("Electronics")
                .build();
        
        Product product2 = Product.builder()
                .id("2")
                .name("Mouse")
                .price(25.0)
                .category("Accessories")
                .build();
        
        when(productUseCase.getAllProducts())
                .thenReturn(Flux.just(product1, product2));
        
        // Act & Assert
        StepVerifier.create(productUseCase.getAllProducts())
                .expectNext(product1)
                .expectNext(product2)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debe obtener producto por ID")
    void testGetProductById() {
        // Arrange
        Product product = Product.builder()
                .id("1")
                .name("Laptop")
                .price(1200.0)
                .category("Electronics")
                .build();
        
        when(productUseCase.getProductById("1"))
                .thenReturn(Mono.just(product));
        
        // Act & Assert
        StepVerifier.create(productUseCase.getProductById("1"))
                .expectNext(product)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debe retornar vacío cuando producto no existe")
    void testGetProductByIdNotFound() {
        // Arrange
        when(productUseCase.getProductById("999"))
                .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(productUseCase.getProductById("999"))
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debe crear producto válido")
    void testCreateProduct() {
        // Arrange
        Product product = Product.builder()
                .name("Laptop")
                .price(1200.0)
                .category("Electronics")
                .build();
        
        Product savedProduct = Product.builder()
                .id("1")
                .name("Laptop")
                .price(1200.0)
                .category("Electronics")
                .build();
        
        when(productUseCase.createProduct(any(Product.class)))
                .thenReturn(Mono.just(savedProduct));
        
        // Act & Assert
        StepVerifier.create(productUseCase.createProduct(product))
                .expectNext(savedProduct)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debe obtener productos caros")
    void testGetExpensiveProducts() {
        // Arrange
        Product expensiveProduct = Product.builder()
                .id("1")
                .name("Laptop")
                .price(1200.0)
                .category("Electronics")
                .build();
        
        when(productUseCase.getExpensiveProducts())
                .thenReturn(Flux.just(expensiveProduct));
        
        // Act & Assert
        StepVerifier.create(productUseCase.getExpensiveProducts())
                .expectNext(expensiveProduct)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debe eliminar producto")
    void testDeleteProduct() {
        // Arrange
        when(productUseCase.deleteProduct("1"))
                .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(productUseCase.deleteProduct("1"))
                .verifyComplete();
    }
}
