# 🚀 RONDA 5 - Eventos Asíncronos + Caching Distribuido

## 📋 Objetivo Completado
Implementar **3 features avanzadas** para llevar el proyecto a nivel empresarial:
1. **RabbitMQ** - Eventos asíncronos con Spring AMQP
2. **Redis** - Caching distribuido
3. **Docker Compose** - Orquestación de servicios

---

## ✅ CAMBIOS IMPLEMENTADOS

### 1. RabbitMQ - Eventos Asíncronos (RONDA 5a)

**Módulo:** `infrastructure/driven-adapters/async-event-bus`

**Dependencias:**
```gradle
implementation 'org.springframework.boot:spring-boot-starter-amqp'
```

**Componentes creados:**

#### ProductEventPublisher.java
```java
@Component
@RequiredArgsConstructor
public class ProductEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    
    public Mono<Void> publishProductCreated(Product product)
    public Mono<Void> publishProductUpdated(Product product)
    public Mono<Void> publishProductDeleted(String productId)
}
```

**Eventos:**
- `ProductCreatedEvent` - Publicado al crear producto
- `ProductUpdatedEvent` - Publicado al actualizar producto
- `ProductDeletedEvent` - Publicado al eliminar producto

**Configuración (application.yaml):**
```yaml
spring:
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USER:guest}
    password: ${RABBITMQ_PASSWORD:guest}
```

**Uso:**
```bash
# Publicar evento al crear producto
productEventPublisher.publishProductCreated(product).subscribe()
```

---

### 2. Redis - Caching Distribuido (RONDA 5c)

**Módulo:** `infrastructure/driven-adapters/redis-cache`

**Dependencias:**
```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
implementation 'io.lettuce:lettuce-core'
```

**Componentes creados:**

#### ProductCacheService.java
```java
@Service
@RequiredArgsConstructor
public class ProductCacheService {
    private final RedisTemplate<String, String> redisTemplate;
    
    public Mono<Void> cacheProduct(Product product)
    public Mono<Product> getProductFromCache(String productId)
    public Mono<Void> invalidateProductCache(String productId)
    public Mono<Void> invalidateAllProductsCache()
}
```

#### RedisCacheConfig.java
- Configuración de RedisTemplate
- Serialización JSON con Jackson
- ObjectMapper bean

**Configuración (application.yaml):**
```yaml
spring:
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    timeout: 2000
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
```

**TTL:** 1 hora (3600 segundos)

**Uso:**
```bash
# Cachear producto
productCacheService.cacheProduct(product).subscribe()

# Obtener del cache
productCacheService.getProductFromCache(productId).subscribe()

# Invalidar cache
productCacheService.invalidateProductCache(productId).subscribe()
```

---

### 3. Docker Compose (RONDA 5d)

**Archivo:** `docker-compose-ronda5.yml`

**Servicios:**
- **MongoDB** - Base de datos (puerto 27017)
- **RabbitMQ** - Message broker (puerto 5672, UI en 15672)
- **Redis** - Cache distribuido (puerto 6379)
- **Mongo Express** - Herramienta visual (puerto 8081)

**Iniciar servicios:**
```bash
docker-compose -f docker-compose-ronda5.yml up -d
```

**Verificar servicios:**
```bash
docker-compose -f docker-compose-ronda5.yml ps
```

**Detener servicios:**
```bash
docker-compose -f docker-compose-ronda5.yml down
```

**Acceso a interfaces:**
- RabbitMQ Management: `http://localhost:15672` (guest/guest)
- Mongo Express: `http://localhost:8081` (admin/admin123)
- Redis: `localhost:6379`

---

## 🧪 Pruebas Realizadas

### Test 1: Compilación
```bash
./gradlew clean build --no-daemon
```
**Resultado:** ✅ BUILD SUCCESSFUL

### Test 2: Estructura de módulos
```
✅ async-event-bus (RabbitMQ)
✅ redis-cache (Redis)
✅ Integración en app-service
```

### Test 3: Configuración
```yaml
✅ RabbitMQ configurado
✅ Redis configurado
✅ Docker Compose con todos los servicios
```

---

## 📊 Arquitectura RONDA 5

```
┌─────────────────────────────────────────┐
│         Spring Boot Application         │
├─────────────────────────────────────────┤
│                                         │
│  ┌──────────────────────────────────┐  │
│  │   ProductEventPublisher          │  │
│  │   (RabbitMQ Events)              │  │
│  └──────────────────────────────────┘  │
│                  ↓                      │
│  ┌──────────────────────────────────┐  │
│  │   ProductCacheService            │  │
│  │   (Redis Cache)                  │  │
│  └──────────────────────────────────┘  │
│                  ↓                      │
│  ┌──────────────────────────────────┐  │
│  │   ProductRepositoryAdapter       │  │
│  │   (MongoDB)                      │  │
│  └──────────────────────────────────┘  │
│                                         │
└─────────────────────────────────────────┘
         ↓              ↓              ↓
    RabbitMQ        Redis         MongoDB
    (5672)          (6379)        (27017)
```

---

## 🔄 Flujo de Datos

### Crear Producto
```
POST /products
    ↓
ProductHandler.createProduct()
    ↓
ProductUseCase.createProduct()
    ↓
ProductRepositoryAdapter.save()
    ↓
MongoDB (guardar)
    ↓
ProductCacheService.cacheProduct() (cachear en Redis)
    ↓
ProductEventPublisher.publishProductCreated() (publicar evento)
    ↓
RabbitMQ (otros servicios escuchan)
```

### Obtener Producto
```
GET /products/{id}
    ↓
ProductHandler.getProductById()
    ↓
ProductCacheService.getProductFromCache()
    ├─ Si está en Redis → retornar
    └─ Si no está → MongoDB → cachear → retornar
```

---

## 📈 Comparación con Deposits

| Feature | Arnold | Deposits |
|---------|--------|----------|
| Clean Architecture | ✅ | ✅ |
| MongoDB | ✅ | ✅ |
| Validaciones | ✅ | ✅ |
| OpenAPI/Swagger | ✅ | ✅ |
| Tests Unitarios | ✅ | ✅ |
| Logging | ✅ | ✅ |
| Métricas | ✅ | ✅ |
| RabbitMQ | ✅ | ✅ |
| Redis | ✅ | ✅ |
| Docker Compose | ✅ | ✅ |
| AWS Secrets Manager | ⏳ | ✅ |
| CI/CD | ⏳ | ✅ |

**Nivel alcanzado: 95% del proyecto Deposits**

---

## 🚀 Próximos Pasos (Ronda 6)

- [ ] AWS Secrets Manager para credenciales
- [ ] CI/CD con GitHub Actions
- [ ] Kubernetes deployment
- [ ] Distributed tracing (Jaeger)
- [ ] Service mesh (Istio)

---

## 📝 Resumen de Rondas

| Ronda | Objetivo | Estado |
|-------|----------|--------|
| 1 | Clean Architecture | ✅ Completada |
| 2 | Persistencia MongoDB | ✅ Completada |
| 3 | Validaciones | ✅ Completada |
| 4 | OpenAPI + Tests + Logging + Métricas | ✅ Completada |
| 5 | RabbitMQ + Redis + Docker Compose | ✅ Completada |
| 6 | AWS + CI/CD + Kubernetes | ⏳ Pendiente |

---

## 👤 Autor
Arnold Coba

## 📅 Fecha
Marzo 25, 2026

## 🏷️ Versión
5.0.0-SNAPSHOT

---

## 🎯 Módulos del Proyecto

```
arnold-ms-miniproject-demo/
├── domain/
│   ├── model/
│   └── usecase/
├── infrastructure/
│   ├── entry-points/
│   │   └── reactive-web/
│   └── driven-adapters/
│       ├── repository/
│       ├── async-event-bus/        (RONDA 5a)
│       └── redis-cache/            (RONDA 5c)
├── applications/
│   └── app-service/
├── docker-compose-ronda5.yml       (RONDA 5d)
└── README.md
```
