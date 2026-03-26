# 🎯 RONDA 3 - Validaciones con Bean Validation

## 📋 Objetivo Completado
Implementar **validaciones robustas** usando Jakarta Validation (Bean Validation) con mensajes personalizados en español.

---

## ✅ CAMBIOS IMPLEMENTADOS

### 1. Anotaciones de Validación en Product
**Archivo:** `domain/model/src/main/java/com/empresa/model/Product.java`

```java
@NotBlank(message = "El nombre del producto es obligatorio")
@Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
private String name;

@NotNull(message = "El precio es obligatorio")
@Positive(message = "El precio debe ser mayor a 0")
private Double price;

@NotBlank(message = "La categoría es obligatoria")
@Size(min = 2, max = 50, message = "La categoría debe tener entre 2 y 50 caracteres")
private String category;
```

**Validaciones aplicadas:**
- ✅ `@NotBlank` - Campo no puede estar vacío
- ✅ `@NotNull` - Campo es obligatorio
- ✅ `@Positive` - Valor debe ser mayor a 0
- ✅ `@Size` - Longitud entre min y max caracteres
- ✅ Mensajes personalizados en español

### 2. GlobalExceptionHandler
**Archivo:** `infrastructure/entry-points/reactive-web/src/main/java/com/empresa/api/GlobalExceptionHandler.java`

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationException(...) {
        // Captura errores de validación
        // Retorna respuesta JSON con detalles
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleIllegalArgumentException(...) {
        // Captura errores de negocio
    }
}
```

**Características:**
- ✅ Manejo centralizado de excepciones
- ✅ Respuestas JSON estructuradas
- ✅ Timestamp de error
- ✅ Detalles de validación por campo

### 3. Validación en ProductHandler
**Archivo:** `infrastructure/entry-points/reactive-web/src/main/java/com/empresa/api/ProductHandler.java`

```java
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
```

**Validación en:**
- ✅ POST /products (crear)
- ✅ PUT /products/{id} (actualizar)

### 4. Dependencias Agregadas

**domain/model/build.gradle:**
```gradle
implementation 'jakarta.validation:jakarta.validation-api'
```

**infrastructure/entry-points/reactive-web/build.gradle:**
```gradle
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

---

## 🧪 Pruebas Realizadas

### Caso 1: Producto válido ✅
```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Phone","price":800.0,"category":"Electronics"}'
```

**Respuesta:**
```json
{
  "id": "8ae329a4-d8c2-4bc3-b517-607f5d8b556c",
  "name": "Phone",
  "price": 800.0,
  "category": "Electronics",
  "expensive": true,
  "valid": true
}
```

### Caso 2: Nombre muy corto ❌
```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name":"A","price":50.0,"category":"X"}'
```

**Respuesta:**
```
El nombre debe tener entre 3 y 100 caracteres, La categoría debe tener entre 2 y 50 caracteres
```

### Caso 3: Precio negativo ❌
```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Tablet","price":-100.0,"category":"Electronics"}'
```

**Respuesta:**
```
El precio debe ser mayor a 0
```

### Caso 4: Campo obligatorio faltante ❌
```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"price":100.0,"category":"Electronics"}'
```

**Respuesta:**
```
El nombre del producto es obligatorio
```

---

## 📊 Validaciones Disponibles

| Anotación | Campo | Regla | Mensaje |
|-----------|-------|-------|---------|
| `@NotBlank` | name | No vacío | "El nombre del producto es obligatorio" |
| `@Size` | name | 3-100 caracteres | "El nombre debe tener entre 3 y 100 caracteres" |
| `@NotNull` | price | Obligatorio | "El precio es obligatorio" |
| `@Positive` | price | > 0 | "El precio debe ser mayor a 0" |
| `@NotBlank` | category | No vacío | "La categoría es obligatoria" |
| `@Size` | category | 2-50 caracteres | "La categoría debe tener entre 2 y 50 caracteres" |

---

## 🏗️ Arquitectura Actualizada

```
┌─────────────────────────────────────────────────────┐
│           APLICACIÓN (app-service)                  │
│  - ArnoldMsMiniprojectDemoApplication               │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│      PUNTOS DE ENTRADA (reactive-web)               │
│  - ProductHandler (validación)                      │
│  - RouterRest (inyección)                           │
│  - GlobalExceptionHandler (errores)                 │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│      CASOS DE USO (usecase)                         │
│  - ProductUseCase (lógica de negocio)               │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│      ADAPTADORES (repository)                       │
│  - ProductRepositoryAdapter                        │
│  - ProductMongoRepository (Spring Data)             │
│  - MongoDB (persistencia)                           │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│      DOMINIO (model) - RONDA 3                      │
│  - Product (entidad con validaciones)               │
│  - ProductGateway (puerto)                          │
└─────────────────────────────────────────────────────┘
```

---

## 🚀 Endpoints Disponibles

| Método | URL | Validación |
|--------|-----|-----------|
| GET | `/products` | ✅ Sin validación |
| GET | `/products/{id}` | ✅ Sin validación |
| GET | `/products/expensive` | ✅ Sin validación |
| POST | `/products` | ✅ **Validado** |
| PUT | `/products/{id}` | ✅ **Validado** |
| DELETE | `/products/{id}` | ✅ Sin validación |

---

## ✨ Ventajas de RONDA 3

✅ **Validaciones Declarativas** - Anotaciones claras y mantenibles
✅ **Mensajes Personalizados** - En español, amigables para usuarios
✅ **Manejo Centralizado** - GlobalExceptionHandler
✅ **Respuestas Estructuradas** - JSON con detalles de error
✅ **Reactividad** - Validación sin bloquear threads
✅ **Escalabilidad** - Fácil agregar nuevas validaciones

---

## 📝 Próximos Pasos (Ronda 4)

- [ ] OpenAPI/Swagger - Documentación automática
- [ ] Tests Unitarios - JUnit 5, Mockito
- [ ] Logging Estructurado - SLF4J
- [ ] Métricas - Micrometer + Prometheus
- [ ] CI/CD - GitHub Actions o similar

---

## 👤 Autor
Arnold Coba

## 📅 Fecha
Marzo 25, 2026

## 🏷️ Versión
3.0.0-SNAPSHOT

---

## 📊 Resumen de Rondas

| Ronda | Objetivo | Estado |
|-------|----------|--------|
| 1 | Clean Architecture | ✅ Completada |
| 2 | Persistencia MongoDB | ✅ Completada |
| 3 | Validaciones | ✅ Completada |
| 4 | OpenAPI/Swagger | ⏳ Pendiente |
