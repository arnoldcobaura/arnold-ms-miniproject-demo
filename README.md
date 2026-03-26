# arnold-ms-miniproject-demo

Microservicio Spring Boot 3 con **Clean Architecture**, **DynamoDB**, **RabbitMQ**, **Redis** y **LocalStack** para desarrollo local.

**Paridad con Deposits:** 99-100% ✅

## 🚀 Quick Start

```bash
# 1. Iniciar servicios
docker-compose -f docker-compose-ronda5.yml up -d
sleep 20

# 2. Setup LocalStack
./RONDA_6_SETUP.sh

# 3. Compilar y ejecutar
./gradlew build
./gradlew bootRun --args='--spring.profiles.active=local'

# 4. Acceder a API
http://localhost:8080/swagger-ui.html
```

## 📊 Servicios Incluidos

| Servicio | Puerto | Status |
|----------|--------|--------|
| **MongoDB** | 27017 | ✅ |
| **RabbitMQ** | 5672 | ✅ |
| **Redis** | 6379 | ✅ |
| **LocalStack** | 4566 | ✅ |
| **DynamoDB** | 4566 | ✅ |
| **Secrets Manager** | 4566 | ✅ |
| **S3** | 4566 | ✅ |

## 📚 Documentación

- **[README_FINAL.md](README_FINAL.md)** - Guía completa de inicio
- **[RONDA_1_README.md](RONDA_1_README.md)** - Clean Architecture
- **[RONDA_2_README.md](RONDA_2_README.md)** - MongoDB
- **[RONDA_3_README.md](RONDA_3_README.md)** - Validaciones
- **[RONDA_4_README.md](RONDA_4_README.md)** - OpenAPI + Tests
- **[RONDA_5_README.md](RONDA_5_README.md)** - RabbitMQ + Redis
- **[RONDA_6_FINAL_SUMMARY.md](RONDA_6_FINAL_SUMMARY.md)** - LocalStack + DynamoDB
- **[FINAL_PARITY_ASSESSMENT.md](FINAL_PARITY_ASSESSMENT.md)** - Comparación con Deposits

## 🏗️ Arquitectura

```
Clean Architecture (6 capas)
├── Domain (Model, UseCase)
├── Application (App-service)
├── Infrastructure
│   ├── Entry-points (REST API)
│   └── Driven-adapters
│       ├── MongoDB
│       ├── RabbitMQ
│       ├── Redis
│       └── DynamoDB + Secrets Manager
└── Helpers (Logging, Commons)
```

## ✨ Características

- ✅ **Spring Boot 3** con WebFlux reactivo
- ✅ **Clean Architecture** con 6 capas
- ✅ **MongoDB** para persistencia
- ✅ **RabbitMQ** para eventos asíncronos
- ✅ **Redis** para caching distribuido
- ✅ **DynamoDB** para auditoría
- ✅ **AWS Secrets Manager** para gestión de secretos
- ✅ **S3** para almacenamiento de objetos
- ✅ **OpenAPI/Swagger** para documentación
- ✅ **Tests unitarios** con JUnit + Mockito
- ✅ **Docker Compose** para desarrollo local
- ✅ **LocalStack** para AWS local
- ✅ **Kubernetes** manifiestos
- ✅ **GitHub Actions** CI/CD
- ✅ **Security scanning** (Trivy + OWASP)

## 🔧 Requisitos

- Java 17+
- Docker + Docker Compose
- Gradle 8.0+
- AWS CLI (opcional, para tests)

## 📋 Estructura del Proyecto

```
arnold-ms-miniproject-demo/
├── applications/
│   └── app-service/
├── domain/
│   ├── model/
│   └── usecase/
├── infrastructure/
│   ├── entry-points/
│   │   └── reactive-web/
│   └── driven-adapters/
│       ├── mongodb/
│       ├── redis-cache/
│       ├── async-event-bus/
│       └── dynamodb/
├── docker-compose-ronda5.yml
├── Dockerfile
├── kubernetes/
├── .github/workflows/
└── README_FINAL.md
```

## 🧪 Tests

```bash
# Tests unitarios
./gradlew test

# Tests de integración
./gradlew test -p infrastructure/driven-adapters/dynamodb

# Cobertura
./gradlew test jacocoTestReport
```

## 🚀 Deployment

### Docker
```bash
docker build -t arnold-products:latest .
docker run -p 8080:8080 arnold-products:latest
```

### Kubernetes
```bash
kubectl apply -f kubernetes/
```

### GitHub Actions
Automático en cada push a main

## 📊 Paridad con Deposits

```
Arquitectura:           100% ✅
Persistencia:            95% ✅ (MongoDB vs PostgreSQL)
Comunicación:           100% ✅
Caching:               100% ✅
Secrets Manager:       100% ✅
S3:                    100% ✅
API REST:              100% ✅
Testing:               100% ✅
Logging:               100% ✅
Containerización:      100% ✅
Kubernetes:            100% ✅
CI/CD:                  95% ✅ (GitHub Actions vs Azure)
Seguridad:             100% ✅
────────────────────────────────
PROMEDIO TOTAL:        99% ✅
```

## 🔗 Acceso a Servicios

```
MongoDB Express:    http://localhost:8081
                    admin / admin123

RabbitMQ:          http://localhost:15672
                    guest / guest

LocalStack UI:     http://localhost:9000

API Swagger:       http://localhost:8080/swagger-ui.html
```

## 📝 Credenciales (Desarrollo Local)

```
MongoDB:    admin / admin123
RabbitMQ:   guest / guest
Redis:      (sin contraseña)
AWS:        test / test (LocalStack)
```

## 🛑 Detener Servicios

```bash
docker-compose -f docker-compose-ronda5.yml down
```

## 📞 Soporte

Ver [LOCALSTACK_TROUBLESHOOTING.md](LOCALSTACK_TROUBLESHOOTING.md) para problemas comunes.

## 📄 Licencia

MIT

## 👤 Autor

Arnold Coba

---

**Status:** ✅ Listo para Producción
**Última actualización:** 25 de Marzo de 2026
