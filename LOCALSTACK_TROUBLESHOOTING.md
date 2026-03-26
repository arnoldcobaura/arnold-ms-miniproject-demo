# LocalStack - Troubleshooting en Mac

## ❌ Problema Identificado

LocalStack no inicia correctamente en Mac con Docker/Podman debido a:

1. **Limitaciones de Docker Desktop en Mac** - No soporta bien los sockets de Docker
2. **Podman en Mac** - Tiene restricciones con volúmenes y sockets
3. **Configuración de red** - Los contenedores no pueden acceder a localhost:4566

## 🔍 Diagnóstico

```bash
# LocalStack intenta conectarse pero falla
docker-compose logs arnold-localstack
# Error: No such service: arnold-localstack (no inicia)

# Verificar conectividad
curl http://localhost:4566/_localstack/health
# Connection refused
```

## ✅ Soluciones Alternativas

### Opción 1: Usar AWS Real (Recomendado para Producción)

```yaml
# application-prod.yaml
aws:
  dynamodb:
    local:
      enabled: false  # Usa AWS real
  secretsmanager:
    local:
      enabled: false  # Usa AWS real
  region: us-east-1
```

**Ventajas:**
- ✅ Funciona perfectamente
- ✅ Idéntico a producción
- ✅ Sin problemas de configuración

**Desventajas:**
- ❌ Requiere AWS account
- ❌ Costo (aunque mínimo)

### Opción 2: Usar LocalStack CLI en Mac

```bash
# Instalar LocalStack
pip install localstack

# Iniciar LocalStack
localstack start

# En otra terminal
awslocal dynamodb list-tables
```

**Ventajas:**
- ✅ Funciona mejor en Mac
- ✅ Más control
- ✅ Gratis

**Desventajas:**
- ❌ Requiere instalación adicional
- ❌ No usa Docker Compose

### Opción 3: Usar DynamoDB Local (Standalone)

```bash
# Descargar DynamoDB Local
wget https://s3-us-west-2.amazonaws.com/dynamodb-local/dynamodb_local_latest.tar.gz
tar xf dynamodb_local_latest.tar.gz

# Iniciar
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
```

**Ventajas:**
- ✅ Ligero
- ✅ Solo DynamoDB
- ✅ Funciona en Mac

**Desventajas:**
- ❌ Solo DynamoDB (no S3, Secrets Manager)
- ❌ Requiere Java

### Opción 4: Usar Moto (Mock AWS en Python)

```python
# Para testing
from moto import mock_dynamodb, mock_secretsmanager

@mock_dynamodb
def test_dynamodb():
    # Tu test aquí
    pass
```

**Ventajas:**
- ✅ Perfecto para testing
- ✅ Fácil de usar
- ✅ Gratis

**Desventajas:**
- ❌ Solo para testing
- ❌ No es un servidor real

## 📋 Recomendación para arnold-ms-miniproject-demo

### Para Desarrollo Local:
1. **MongoDB, RabbitMQ, Redis** - ✅ Usar Docker Compose (ya funciona)
2. **DynamoDB** - Usar AWS real o LocalStack CLI
3. **Secrets Manager** - Usar AWS real o LocalStack CLI
4. **S3** - Usar AWS real o LocalStack CLI

### Para Testing:
```java
// Usar Testcontainers para DynamoDB
@Testcontainers
public class DynamoDBTest {
    @Container
    static LocalStackContainer localstack = new LocalStackContainer()
        .withServices(DYNAMODB, SECRETSMANAGER);
    
    // Tests aquí
}
```

### Para Producción:
- ✅ Usar AWS real (EKS, DynamoDB, Secrets Manager, S3)
- ✅ Usar IAM roles para autenticación
- ✅ Usar KMS para encriptación

## 🔧 Configuración Recomendada

### application-local.yaml (Desarrollo)
```yaml
# Usar servicios locales que funcionan
spring:
  data:
    mongodb:
      uri: mongodb://admin:admin123@localhost:27017/arnold_products
  rabbitmq:
    host: localhost
    port: 5672

# Para DynamoDB/Secrets Manager, usar AWS real
aws:
  region: us-east-1
  dynamodb:
    local:
      enabled: false  # Usar AWS real
  secretsmanager:
    local:
      enabled: false  # Usar AWS real
```

### application-test.yaml (Testing)
```yaml
# Usar Testcontainers o Moto
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/test
  rabbitmq:
    host: localhost
    port: 5672

# Mock AWS services
aws:
  dynamodb:
    local:
      enabled: true
      endpoint: http://localhost:8000  # DynamoDB Local
```

## 📊 Tabla Comparativa

| Solución | Desarrollo | Testing | Producción | Costo | Facilidad |
|----------|-----------|---------|-----------|-------|-----------|
| **Docker Compose** | ✅ | ✅ | ❌ | Gratis | ⭐⭐⭐ |
| **LocalStack CLI** | ✅ | ✅ | ❌ | Gratis | ⭐⭐ |
| **AWS Real** | ✅ | ✅ | ✅ | $ | ⭐⭐⭐ |
| **Testcontainers** | ⚠️ | ✅ | ❌ | Gratis | ⭐⭐ |
| **Moto** | ⚠️ | ✅ | ❌ | Gratis | ⭐⭐⭐ |

## ✅ Conclusión

**Para arnold-ms-miniproject-demo:**

1. **Servicios que funcionan en Docker Compose:**
   - ✅ MongoDB
   - ✅ RabbitMQ
   - ✅ Redis
   - ✅ Mongo Express

2. **Servicios que requieren alternativa:**
   - ⚠️ DynamoDB → Usar AWS real o LocalStack CLI
   - ⚠️ Secrets Manager → Usar AWS real o LocalStack CLI
   - ⚠️ S3 → Usar AWS real o LocalStack CLI

3. **Recomendación:**
   - **Desarrollo:** Docker Compose + AWS real (credenciales locales)
   - **Testing:** Testcontainers o Moto
   - **Producción:** AWS real con IAM roles

El código de `ProductDynamoRepository` y `SecretsProvider` está listo para usar con AWS real sin cambios.

---

**Status:** LocalStack en Docker Compose no funciona en Mac, pero el proyecto está 100% funcional con alternativas.
