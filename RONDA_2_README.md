# 🎯 RONDA 2 - Persistencia con MongoDB

## 📋 Objetivo Completado
Reemplazar la BD en memoria con **MongoDB** para persistencia real.

## 🚀 Opciones de Levantamiento

### Opción 1: Podman (Recomendado para macOS)
```bash
podman-compose up -d
```
Ver detalles en: `RONDA_2_PODMAN_SETUP.md`

### Opción 2: Docker
```bash
docker-compose -f docker-compose.docker.yml up -d
```
Ver detalles en: `RONDA_2_DOCKER_SETUP.md`

---

## ✅ CAMBIOS IMPLEMENTADOS

### 1. Docker Compose
**Archivos:**
- `docker-compose.yml` - Para Podman
- `docker-compose.docker.yml` - Para Docker

**Servicios:**
- MongoDB 7.0 (puerto 27017)
- Mongo Express (puerto 8081)

### 2. Dependencias Actualizadas
**Archivo:** `infrastructure/driven-adapters/repository/build.gradle`
```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-mongodb-reactive'
```

### 3. Spring Data MongoDB Repository
**Archivo:** `infrastructure/driven-adapters/repository/src/main/java/com/empresa/repository/ProductMongoRepository.java`
```java
@Repository
public interface ProductMongoRepository extends ReactiveMongoRepository<Product, String> {
    // Spring Data genera automáticamente todas las operaciones CRUD
}
```

### 4. Adaptador Actualizado
**Archivo:** `infrastructure/driven-adapters/repository/src/main/java/com/empresa/repository/ProductRepositoryAdapter.java`
```java
@Component
public class ProductRepositoryAdapter implements ProductGateway {
    private final ProductMongoRepository mongoRepository;
    
    // Delega todas las operaciones a MongoDB
    // Mantiene la misma interfaz (ProductGateway)
}
```

### 5. Configuración MongoDB
**Archivo:** `applications/app-service/src/main/resources/application.yaml`
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://admin:admin123@localhost:27017/arnold_products?authSource=admin
      auto-index-creation: true
```

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
│  - ProductHandler (maneja HTTP)                     │
│  - RouterRest (define rutas)                        │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│      CASOS DE USO (usecase)                         │
│  - ProductUseCase (lógica de negocio)               │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│      ADAPTADORES (repository) - RONDA 2             │
│  - ProductRepositoryAdapter (implementa gateway)    │
│  - ProductMongoRepository (Spring Data)             │
│  - MongoDB (persistencia real)                      │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│      DOMINIO (model)                                │
│  - Product (entidad)                                │
│  - ProductGateway (puerto)                          │
└─────────────────────────────────────────────────────┘
```

---

## 📊 Credenciales MongoDB

- **Host:** localhost
- **Puerto:** 27017
- **Usuario:** admin
- **Contraseña:** admin123
- **Base de datos:** arnold_products

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
```bash
# Crear producto
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "price": 1200.0,
    "category": "Electronics"
  }'

# Obtener todos
curl http://localhost:8080/products

# Obtener caros
curl http://localhost:8080/products/expensive
```

---

## ✨ Ventajas de RONDA 2

✅ **Persistencia Real** - Datos guardados en MongoDB
✅ **Escalabilidad** - Soporta millones de documentos
✅ **Flexibilidad** - Schema dinámico de MongoDB
✅ **Reactividad** - Spring Data Reactive MongoDB
✅ **Mismo Contrato** - ProductGateway sin cambios
✅ **Fácil Cambio** - Solo cambió la implementación

---

## 📝 Próximos Pasos (Ronda 3)

- [ ] Tests unitarios e integración
- [ ] Validaciones con Bean Validation
- [ ] OpenAPI/Swagger
- [ ] Manejo global de excepciones
- [ ] Logging estructurado
- [ ] Métricas con Micrometer

---

## 👤 Autor
Manuel Coba - Bancolombia

## 📅 Fecha
Marzo 25, 2026

## 🏷️ Versión
2.0.0-SNAPSHOT
