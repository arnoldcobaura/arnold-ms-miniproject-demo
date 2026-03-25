# 🎯 RONDA 1 - Clean Architecture Microservicio de Productos

## 📋 Objetivo Completado
Implementar un microservicio funcional con **Clean Architecture** que demuestre:
- Separación de capas (Dominio, Casos de Uso, Infraestructura)
- Inyección de dependencias
- Programación reactiva con Reactor
- API REST con Spring WebFlux
- Estructura modular con Gradle multi-módulo

---

## ✅ PASOS IMPLEMENTADOS

### PASO 1: Configuración de Módulos
**Archivo:** `settings.gradle`
- Define 5 módulos: `model`, `usecase`, `repository`, `reactive-web`, `app-service`
- Mapea rutas físicas de cada módulo
- Estructura modular lista para escalabilidad

### PASO 2-3: Build de Cada Módulo
**Archivos:** `*/build.gradle`
- `domain/model/build.gradle` - Solo Reactor (sin Spring)
- `domain/usecase/build.gradle` - Depende de model
- `infrastructure/driven-adapters/repository/build.gradle` - Spring Context
- `infrastructure/entry-points/reactive-web/build.gradle` - Spring WebFlux
- `applications/app-service/build.gradle` - Ensambla todo

### PASO 4: Entidad del Dominio
**Archivo:** `domain/model/src/main/java/com/empresa/model/Product.java`
```java
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Product {
    private String id;
    private String name;
    private Double price;
    private String category;
    
    public boolean isExpensive() { return price > 100.0; }
    public boolean isValid() { /* validaciones */ }
}
```
- Clase pura sin anotaciones de frameworks
- Lógica de negocio encapsulada
- Lombok para reducir boilerplate

### PASO 5: Puerto del Dominio
**Archivo:** `domain/model/src/main/java/com/empresa/model/gateway/ProductGateway.java`
```java
public interface ProductGateway {
    Mono<Product> findById(String id);
    Flux<Product> findAll();
    Mono<Product> save(Product product);
    Mono<Void> deleteById(String id);
    Mono<Boolean> existsById(String id);
}
```
- Define contrato sin saber implementación
- Principio de inversión de dependencias
- Reactivo con Mono/Flux

### PASO 6: Casos de Uso
**Archivo:** `domain/usecase/src/main/java/com/empresa/usecase/ProductUseCase.java`
```java
public class ProductUseCase {
    private final ProductGateway productGateway;
    
    public Mono<Product> createProduct(Product product) {
        return Mono.just(product)
            .filter(Product::isValid)
            .switchIfEmpty(Mono.error(...))
            .flatMap(productGateway::save);
    }
    // getAllProducts, getProductById, updateProduct, deleteProduct, getExpensiveProducts
}
```
- Orquesta operaciones del dominio
- Validaciones de negocio
- Sin dependencias de Spring

### PASO 7: Adaptador de Repositorio
**Archivo:** `infrastructure/driven-adapters/repository/src/main/java/com/empresa/repository/ProductRepositoryAdapter.java`
```java
@Component
public class ProductRepositoryAdapter implements ProductGateway {
    private final Map<String, Product> database = new ConcurrentHashMap<>();
    
    @Override
    public Mono<Product> save(Product product) {
        return Mono.fromCallable(() -> {
            if (product.getId() == null) {
                product.setId(UUID.randomUUID().toString());
            }
            database.put(product.getId(), product);
            return product;
        });
    }
    // findById, findAll, deleteById, existsById
}
```
- Implementa ProductGateway
- BD en memoria (ConcurrentHashMap)
- Fácil de cambiar a BD real (MongoDB, PostgreSQL)

### PASO 8a: Handler HTTP
**Archivo:** `infrastructure/entry-points/reactive-web/src/main/java/com/empresa/api/ProductHandler.java`
```java
public class ProductHandler {
    private final ProductUseCase productUseCase;
    
    public Mono<ServerResponse> getAllProducts(ServerRequest request) {
        return ServerResponse.ok()
            .body(productUseCase.getAllProducts(), Product.class);
    }
    // getProductById, createProduct, updateProduct, deleteProduct, getExpensiveProducts
}
```
- Maneja peticiones HTTP
- Delega a casos de uso
- Transforma respuestas

### PASO 8b: Router REST
**Archivo:** `infrastructure/entry-points/reactive-web/src/main/java/com/empresa/api/RouterRest.java`
```java
@Configuration
public class RouterRest {
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
```
- Define rutas HTTP
- Inyecta dependencias
- Configuración de beans

### PASO 9: Configuración
**Archivo:** `applications/app-service/src/main/resources/application.yaml`
```yaml
server:
  port: 8080

spring:
  application:
    name: arnold-ms-miniproject-demo

logging:
  level:
    root: INFO
    com.empresa: DEBUG
```

---

## 🏗️ Arquitectura Implementada

```
┌─────────────────────────────────────────────────────┐
│           APLICACIÓN (app-service)                  │
│  - ArnoldMsMiniprojectDemoApplication               │
│  - @SpringBootApplication                           │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│      PUNTOS DE ENTRADA (reactive-web)               │
│  - ProductHandler (maneja HTTP)                     │
│  - RouterRest (define rutas)                        │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│      CASOS DE USO (usecase)                         │
│  - ProductUseCase (lógica de negocio)               │
│  - Sin dependencias de Spring                       │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│      ADAPTADORES (repository)                       │
│  - ProductRepositoryAdapter (implementa gateway)    │
│  - BD en memoria (ConcurrentHashMap)                │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│      DOMINIO (model)                                │
│  - Product (entidad)                                │
│  - ProductGateway (puerto)                          │
│  - Lógica pura sin frameworks                       │
└─────────────────────────────────────────────────────┘
```

---

## 🚀 Endpoints Disponibles

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/products` | Obtener todos los productos |
| GET | `/products/{id}` | Obtener producto por ID |
| GET | `/products/expensive` | Obtener productos caros (>$100) |
| POST | `/products` | Crear nuevo producto |
| PUT | `/products/{id}` | Actualizar producto |
| DELETE | `/products/{id}` | Eliminar producto |

### Ejemplo de Petición
# Crear producto
```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "price": 1200.0,
    "category": "Electronics"
  }'
```

# Obtener todos
```bash
curl http://localhost:8080/products
```

# Obtener caros
```bash
curl http://localhost:8080/products/expensive
```

---

## ✅ Compilación y Ejecución

```bash
# Compilar
./gradlew clean build

# Ejecutar
./gradlew bootRun

# Resultado esperado
# BUILD SUCCESSFUL in 10s
# Netty started on port 8080 (http)
# Started ArnoldMsMiniprojectDemoApplication in 0.741 seconds
```

---

## 📊 Características Implementadas

✅ **Clean Architecture**
- Separación clara de capas
- Dominio independiente de frameworks
- Inyección de dependencias

✅ **Programación Reactiva**
- Mono/Flux de Reactor
- No bloquea threads
- Escalable

✅ **Modular**
- 5 módulos independientes
- Fácil de mantener y expandir
- Gradle multi-módulo

✅ **Spring Boot 3.5.12**
- WebFlux para API reactiva
- DevTools para desarrollo
- Lombok para reducir código

✅ **Documentación**
- Comentarios PASO X en cada archivo
- README completo
- Estructura clara

---

## 🎯 Principios Aplicados

1. **Inversión de Dependencias** - El dominio define puertos, la infraestructura implementa
2. **Separación de Responsabilidades** - Cada capa tiene un rol específico
3. **DRY (Don't Repeat Yourself)** - Código reutilizable y limpio
4. **SOLID** - Especialmente Single Responsibility y Dependency Inversion
5. **Reactividad** - Streams no bloqueantes con Reactor

---

## 📝 Estructura de Directorios

```
arnold-ms-miniproject-demo/
├── domain/
│   ├── model/
│   │   ├── build.gradle
│   │   └── src/main/java/com/empresa/model/
│   │       ├── Product.java (PASO 4)
│   │       └── gateway/ProductGateway.java (PASO 5)
│   └── usecase/
│       ├── build.gradle
│       └── src/main/java/com/empresa/usecase/
│           └── ProductUseCase.java (PASO 6)
├── infrastructure/
│   ├── driven-adapters/
│   │   └── repository/
│   │       ├── build.gradle
│   │       └── src/main/java/com/empresa/repository/
│   │           └── ProductRepositoryAdapter.java (PASO 7)
│   └── entry-points/
│       └── reactive-web/
│           ├── build.gradle
│           └── src/main/java/com/empresa/api/
│               ├── ProductHandler.java (PASO 8a)
│               └── RouterRest.java (PASO 8b)
├── applications/
│   └── app-service/
│       ├── build.gradle
│       ├── src/main/java/com/empresa/
│       │   └── ArnoldMsMiniprojectDemoApplication.java
│       └── src/main/resources/
│           └── application.yaml (PASO 9)
├── settings.gradle (PASO 1)
└── build.gradle (raíz)
```

---

## 🔄 Flujo de una Petición

1. **HTTP Request** → `/products` (GET)
2. **Router** → Mapea a `ProductHandler.getAllProducts()`
3. **Handler** → Delega a `ProductUseCase.getAllProducts()`
4. **UseCase** → Llama a `ProductGateway.findAll()`
5. **Adapter** → Implementa y retorna datos de BD en memoria
6. **UseCase** → Retorna Flux<Product>
7. **Handler** → Convierte a ServerResponse
8. **HTTP Response** → JSON con productos

---

## ✨ Próximos Pasos (Ronda 2)

- [ ] Tests unitarios (JUnit 5, Mockito, StepVerifier)
- [ ] Persistencia en BD real (MongoDB/PostgreSQL)
- [ ] Validaciones avanzadas (Bean Validation)
- [ ] Documentación OpenAPI/Swagger
- [ ] Manejo de excepciones global
- [ ] Logging estructurado
- [ ] Métricas con Micrometer

---

## 📌 Estado: ✅ RONDA 1 COMPLETADA

**Compilación:** ✅ BUILD SUCCESSFUL
**Ejecución:** ✅ Netty started on port 8080
**Arquitectura:** ✅ Clean Architecture implementada
**Documentación:** ✅ PASO X en cada archivo

---

## 👤 Autor
Manuel Coba - Bancolombia

## 📅 Fecha
Marzo 25, 2026

## 🏷️ Versión
1.0.0-SNAPSHOT
