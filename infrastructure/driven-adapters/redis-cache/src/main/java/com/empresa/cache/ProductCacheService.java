package com.empresa.cache;

import com.empresa.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * RONDA 5c: PRODUCT CACHE SERVICE - CACHING CON REDIS
 * 
 * Servicio para cachear productos en Redis
 * Reduce carga en MongoDB con acceso rápido a datos frecuentes
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCacheService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String CACHE_PREFIX = "product:";
    private static final String ALL_PRODUCTS_KEY = "products:all";
    private static final long CACHE_TTL = 3600; // 1 hora en segundos
    
    public Mono<Void> cacheProduct(Product product) {
        log.debug("Cacheando producto: {}", product.getId());
        return Mono.fromRunnable(() -> {
            try {
                String key = CACHE_PREFIX + product.getId();
                String value = objectMapper.writeValueAsString(product);
                redisTemplate.opsForValue().set(key, value, CACHE_TTL, TimeUnit.SECONDS);
                log.debug("Producto cacheado exitosamente: {}", product.getId());
            } catch (Exception e) {
                log.error("Error cacheando producto: {}", e.getMessage(), e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
    
    public Mono<Product> getProductFromCache(String productId) {
        log.debug("Buscando producto en cache: {}", productId);
        return Mono.fromCallable(() -> {
            String key = CACHE_PREFIX + productId;
            String value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                log.debug("Producto encontrado en cache: {}", productId);
                return objectMapper.readValue(value, Product.class);
            }
            log.debug("Producto no encontrado en cache: {}", productId);
            return null;
        }).subscribeOn(Schedulers.boundedElastic());
    }
    
    public Mono<Void> invalidateProductCache(String productId) {
        log.debug("Invalidando cache del producto: {}", productId);
        return Mono.fromRunnable(() -> {
            String key = CACHE_PREFIX + productId;
            redisTemplate.delete(key);
            log.debug("Cache invalidado: {}", productId);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
    
    public Mono<Void> invalidateAllProductsCache() {
        log.debug("Invalidando cache de todos los productos");
        return Mono.fromRunnable(() -> {
            redisTemplate.delete(ALL_PRODUCTS_KEY);
            log.debug("Cache de todos los productos invalidado");
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
