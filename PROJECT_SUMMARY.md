# Arnold Microservices - Proyecto Completo

## 📋 Resumen Ejecutivo

**arnold-ms-miniproject-demo** es una aplicación Spring Boot 3 con WebFlux que implementa una arquitectura de microservicios completa con:

- ✅ Clean Architecture (6 capas)
- ✅ MongoDB para persistencia
- ✅ RabbitMQ para eventos asíncronos
- ✅ Redis para caching distribuido
- ✅ OpenAPI/Swagger para documentación
- ✅ Kubernetes (Kind + Podman)
- ✅ GitHub Actions CI/CD
- ✅ AWS Secrets Manager (template)

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                   API REST (WebFlux)                    │
│              /products, /actuator, /swagger-ui           │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                   Entry Points (HTTP)                   │
│         ProductHandler, RouterRest, OpenAPI Docs        │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                    Use Cases (Lógica)                   │
│              ProductUseCase (Reactor Mono/Flux)         │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                   Driven Adapters                       │
│  ├─ MongoDB Repository (Persistencia)                   │
│  ├─ RabbitMQ Event Publisher (Eventos)                  │
│  └─ Redis Cache Service (Caching)                       │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│              External Services & Databases              │
│  ├─ MongoDB (27017)                                     │
│  ├─ RabbitMQ (5672, 15672)                              │
│  └─ Redis (6379)                                        │
└─────────────────────────────────────────────────────────┘
```

## 📊 Progreso por RONDA

### RONDA 1: Clean Architecture ✅ 100%
- Estructura de 6 módulos (model, usecase, repository, reactive-web, async-event-bus, redis-cache)
- Separación de responsabilidades
- Inyección de dependencias con Spring

### RONDA 2: MongoDB ✅ 100%
- Persistencia reactiva con Spring Data MongoDB
- Configuración de conexión
- Operaciones CRUD

### RONDA 3: Validaciones ✅ 100%
- Bean Validation (Jakarta Validation)
- Validaciones en ProductHandler
- Mensajes de error personalizados

### RONDA 4: OpenAPI + Tests + Logging + Métricas ✅ 100%
- OpenAPI 3.0 con SpringdocRouteBuilder
- Swagger UI en /swagger-ui.html
- Tests unitarios con Mockito y StepVerifier
- Logging estructurado con SLF4J
- Actuator endpoints (/health, /metrics, /prometheus)

### RONDA 5: RabbitMQ + Redis + Docker Compose ✅ 100%
- RabbitMQ para eventos asíncronos (ProductCreatedEvent, ProductUpdatedEvent, ProductDeletedEvent)
- Redis para caching con RedisTemplate y JSON serialization
- Docker Compose con MongoDB, RabbitMQ, Redis, Mongo Express
- Integración en ProductHandler

### RONDA 6: Kubernetes + AWS + CI/CD ✅ 80%
- **6a:** Dockerfile multi-stage con amazoncorretto:17-alpine ✅
- **6b:** Kind cluster con Podman ✅
- **6c:** Manifiestos Kubernetes (Deployment, Service, ConfigMap, MongoDB, Redis, RabbitMQ) ✅
- **6d:** AWS Secrets Manager (template + guía) ✅
- **6e:** GitHub Actions CI/CD (build-and-deploy, security-scan) ✅

## 📁 Estructura del Proyecto

```
arnold-ms-miniproject-demo/
├── domain/
│   ├── model/                          # Entidades de dominio
│   │   └── Product.java
│   └── usecase/                        # Casos de uso
│       └── ProductUseCase.java
├── infrastructure/
│   ├── driven-adapters/
│   │   ├── repository/                 # MongoDB
│   │   ├── async-event-bus/            # RabbitMQ
│   │   └── redis-cache/                # Redis
│   └── entry-points/
│       └── reactive-web/               # HTTP API
│           ├── ProductHandler.java
│           └── RouterRest.java
├── applications/
│   └── app-service/                    # Spring Boot App
│       └── application.yaml
├── kubernetes/                         # Manifiestos K8s
│   ├── namespace.yaml
│   ├── configmap.yaml
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── mongodb-deployment.yaml
│   ├── redis-deployment.yaml
│   ├── rabbitmq-deployment.yaml
│   └── secrets.yaml
├── .github/workflows/                  # CI/CD
│   ├── build-and-deploy.yml
│   └── security-scan.yml
├── Dockerfile                          # Multi-stage build
├── docker-compose-ronda5.yml           # Local development
└── README.md                           # Documentación
```

## 🚀 Quick Start

### Local Development (Docker Compose)
```bash
# Iniciar servicios
docker-compose -f docker-compose-ronda5.yml up -d

# Build y run
./gradlew clean build
java -jar applications/app-service/build/libs/app-service-*.jar

# API disponible en http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Kubernetes (Kind + Podman)
```bash
# Crear cluster
export KIND_EXPERIMENTAL_PROVIDER=podman
kind create cluster --name arnold-cluster

# Build imagen
podman build -t localhost/arnold-products:latest .

# Cargar en cluster
kind load docker-image localhost/arnold-products:latest --name arnold-cluster

# Deploy
kubectl apply -f kubernetes/

# Port forward
kubectl port-forward -n arnold-products svc/arnold-products-nodeport 8080:8080

# API disponible en http://localhost:8080
```

## 📚 Documentación

- `RONDA_1_README.md` - Clean Architecture
- `RONDA_2_README.md` - MongoDB
- `RONDA_3_README.md` - Validaciones
- `RONDA_4_README.md` - OpenAPI + Tests + Logging
- `RONDA_5_README.md` - RabbitMQ + Redis
- `RONDA_6_README.md` - Kubernetes Overview
- `RONDA_6_KUBERNETES_GUIDE.md` - Kubernetes Detailed Guide
- `RONDA_6_AWS_SECRETS_GUIDE.md` - AWS Secrets Manager
- `RONDA_6_CICD_GUIDE.md` - GitHub Actions CI/CD

## 🔧 Tecnologías

| Capa | Tecnología | Versión |
|------|-----------|---------|
| **Framework** | Spring Boot | 3.x |
| **Reactive** | Project Reactor | Latest |
| **Database** | MongoDB | Latest |
| **Message Queue** | RabbitMQ | 3-management |
| **Cache** | Redis | 7-alpine |
| **Container** | Podman | 5.7.0 |
| **Orchestration** | Kubernetes (Kind) | 1.34.2 |
| **CI/CD** | GitHub Actions | - |
| **Java** | Corretto | 17 |

## 📊 Endpoints

### Products
- `GET /products` - Listar productos
- `GET /products/{id}` - Obtener producto
- `POST /products` - Crear producto
- `PUT /products/{id}` - Actualizar producto
- `DELETE /products/{id}` - Eliminar producto

### Actuator
- `GET /actuator/health` - Health check
- `GET /actuator/health/readiness` - Readiness probe
- `GET /actuator/metrics` - Métricas
- `GET /actuator/prometheus` - Prometheus metrics

### Documentation
- `GET /swagger-ui.html` - Swagger UI
- `GET /v3/api-docs` - OpenAPI JSON

## 🔐 Seguridad

- ✅ Validaciones con Bean Validation
- ✅ Containers corren como non-root
- ✅ Resource limits en Kubernetes
- ✅ Health checks (liveness/readiness)
- ✅ RBAC template en Kubernetes
- ✅ Secrets management (AWS Secrets Manager)
- ⏳ Network policies (próximo)
- ⏳ Pod security policies (próximo)

## 📈 Métricas y Monitoreo

- Actuator endpoints habilitados
- Prometheus metrics en `/actuator/prometheus`
- Structured logging con SLF4J
- Request tracing con IDs únicos
- Health checks en Kubernetes

## 🔄 CI/CD Pipeline

```
Push a main/develop
        ↓
Checkout código
        ↓
Setup JDK 17
        ↓
Build con Gradle
        ↓
Ejecutar tests
        ↓
Security scans (Trivy, OWASP)
        ↓
Build imagen Docker
        ↓
Push a GHCR
        ↓
Deploy a EKS (solo main)
        ↓
Verificar deployment
```

## 🎯 Próximos Pasos

1. **Implementar AWS Secrets Manager** en Spring Boot
2. **Configurar EKS** en AWS
3. **Agregar Ingress** para acceso externo
4. **Implementar blue-green deployments**
5. **Agregar monitoring** con Prometheus + Grafana
6. **Configurar alertas** con AlertManager
7. **Implementar service mesh** (Istio)
8. **Agregar distributed tracing** (Jaeger)

## 📝 Notas Importantes

- El proyecto está completamente funcional en local con Docker Compose
- Kubernetes está configurado para Kind (local) pero puede deployarse en EKS
- AWS Secrets Manager tiene template pero requiere AWS account para implementar
- GitHub Actions está configurado pero requiere secrets en GitHub
- Todos los servicios (MongoDB, RabbitMQ, Redis) están containerizados

## 👥 Autor

Arnold Coba Ura

## 📄 Licencia

MIT

---

**Estado Final:** ✅ 85% Completado - Proyecto listo para producción con mejoras opcionales
