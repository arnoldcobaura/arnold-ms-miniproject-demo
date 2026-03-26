# RONDA 6: LocalStack + DynamoDB + AWS Secrets Manager

## 🎯 Objetivo

Integrar LocalStack (AWS local) con DynamoDB, S3 y Secrets Manager para desarrollo local, logrando paridad completa con Deposits.

## 📋 Componentes

### LocalStack
- **DynamoDB**: Base de datos NoSQL para auditoría y análisis histórico
- **S3**: Almacenamiento de objetos para reportes y archivos
- **Secrets Manager**: Gestión de secretos local
- **KMS**: Encriptación de datos

### Integración con Aplicación
- Configuración de endpoints locales
- Creación automática de tablas
- Gestión de secretos

## 🚀 Quick Start

### 1. Iniciar LocalStack
```bash
docker-compose -f docker-compose-ronda5.yml up -d localstack
```

### 2. Verificar LocalStack
```bash
# Esperar a que esté listo
sleep 10

# Verificar estado
curl -s http://localhost:4566/_localstack/health | jq '.services'

# Acceder a Web UI
open http://localhost:9000
```

### 3. Crear Tabla DynamoDB
```bash
# Crear tabla de auditoría
aws dynamodb create-table \
  --table-name arnold-products-audit \
  --attribute-definitions \
    AttributeName=productId,AttributeType=S \
    AttributeName=timestamp,AttributeType=N \
  --key-schema \
    AttributeName=productId,KeyType=HASH \
    AttributeName=timestamp,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST \
  --endpoint-url http://localhost:4566 \
  --region us-east-1

# Verificar tabla
aws dynamodb list-tables \
  --endpoint-url http://localhost:4566 \
  --region us-east-1
```

### 4. Crear Secretos
```bash
# Crear secret para MongoDB
aws secretsmanager create-secret \
  --name arnold-products/mongodb \
  --description "MongoDB credentials" \
  --secret-string '{
    "username": "admin",
    "password": "admin123",
    "uri": "mongodb://admin:admin123@mongodb:27017/arnold_products"
  }' \
  --endpoint-url http://localhost:4566 \
  --region us-east-1

# Crear secret para RabbitMQ
aws secretsmanager create-secret \
  --name arnold-products/rabbitmq \
  --description "RabbitMQ credentials" \
  --secret-string '{
    "username": "guest",
    "password": "guest",
    "host": "rabbitmq",
    "port": 5672
  }' \
  --endpoint-url http://localhost:4566 \
  --region us-east-1

# Crear secret para Redis
aws secretsmanager create-secret \
  --name arnold-products/redis \
  --description "Redis configuration" \
  --secret-string '{
    "host": "redis",
    "port": 6379
  }' \
  --endpoint-url http://localhost:4566 \
  --region us-east-1
```

### 5. Crear Bucket S3
```bash
aws s3 mb s3://arnold-products-reports \
  --endpoint-url http://localhost:4566 \
  --region us-east-1

# Verificar buckets
aws s3 ls --endpoint-url http://localhost:4566
```

## 🔧 Configuración de la Aplicación

### application-local.yaml
```yaml
# RONDA 6: LocalStack Configuration
aws:
  dynamodb:
    local:
      enabled: true
      endpoint: http://localhost:4566
  secretsmanager:
    local:
      enabled: true
      endpoint: http://localhost:4566
  s3:
    local:
      enabled: true
      endpoint: http://localhost:4566
      bucket: arnold-products-reports
  region: us-east-1
  credentials:
    access-key-id: test
    secret-access-key: test
```

## 📊 Arquitectura LocalStack

```
┌─────────────────────────────────────────────────────┐
│                   LocalStack                        │
│  ┌───────────────────────────────────────────────┐  │
│  │  DynamoDB (4571)                              │  │
│  │  ├─ arnold-products-audit (auditoría)         │  │
│  │  └─ arnold-products-events (eventos)          │  │
│  └───────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────┐  │
│  │  S3 (4566)                                    │  │
│  │  └─ arnold-products-reports (reportes)        │  │
│  └───────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────┐  │
│  │  Secrets Manager (4566)                       │  │
│  │  ├─ arnold-products/mongodb                   │  │
│  │  ├─ arnold-products/rabbitmq                  │  │
│  │  └─ arnold-products/redis                     │  │
│  └───────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────┐  │
│  │  KMS (4566)                                   │  │
│  │  └─ Encriptación de datos                     │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

## 💾 Casos de Uso DynamoDB

### 1. Auditoría de Productos
```java
// Guardar evento cuando se crea un producto
productDynamoRepository.saveAuditEvent(
    productId,
    "CREATED",
    "Product created with name: " + product.getName()
);
```

### 2. Análisis Histórico
```bash
# Consultar eventos de un producto
aws dynamodb query \
  --table-name arnold-products-audit \
  --key-condition-expression "productId = :id" \
  --expression-attribute-values '{":id":{"S":"123"}}' \
  --endpoint-url http://localhost:4566 \
  --region us-east-1
```

### 3. Reportes
```bash
# Exportar datos a S3
aws s3 cp s3://arnold-products-reports/report.json . \
  --endpoint-url http://localhost:4566
```

## 🔐 Secrets Manager

### Recuperar Secretos
```java
@Component
public class SecretsProvider {
    
    private final SecretsManagerClient secretsClient;
    
    public String getSecret(String secretName) {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();
        
        GetSecretValueResponse response = secretsClient.getSecretValue(request);
        return response.secretString();
    }
}
```

### Usar en Configuración
```java
@Configuration
public class MongoDBConfig {
    
    @Bean
    public MongoClient mongoClient(SecretsProvider secretsProvider) {
        String secretJson = secretsProvider.getSecret("arnold-products/mongodb");
        // Parsear JSON y crear cliente
        return MongoClients.create(uri);
    }
}
```

## 📚 Comandos Útiles

### Listar Recursos
```bash
# Tablas DynamoDB
aws dynamodb list-tables --endpoint-url http://localhost:4566

# Buckets S3
aws s3 ls --endpoint-url http://localhost:4566

# Secretos
aws secretsmanager list-secrets --endpoint-url http://localhost:4566
```

### Eliminar Recursos
```bash
# Eliminar tabla
aws dynamodb delete-table \
  --table-name arnold-products-audit \
  --endpoint-url http://localhost:4566

# Eliminar bucket
aws s3 rb s3://arnold-products-reports \
  --endpoint-url http://localhost:4566

# Eliminar secret
aws secretsmanager delete-secret \
  --secret-id arnold-products/mongodb \
  --force-delete-without-recovery \
  --endpoint-url http://localhost:4566
```

## 🧪 Testing con LocalStack

### Test DynamoDB
```java
@SpringBootTest
public class DynamoDBTest {
    
    @Autowired
    private ProductDynamoRepository repository;
    
    @Test
    public void testSaveAuditEvent() {
        repository.saveAuditEvent("123", "CREATED", "Test event")
            .block();
        
        // Verificar en DynamoDB
    }
}
```

### Test Secrets Manager
```java
@SpringBootTest
public class SecretsManagerTest {
    
    @Autowired
    private SecretsProvider secretsProvider;
    
    @Test
    public void testGetSecret() {
        String secret = secretsProvider.getSecret("arnold-products/mongodb");
        assertNotNull(secret);
    }
}
```

## 📊 Comparación: LocalStack vs AWS

| Aspecto | LocalStack | AWS |
|---------|-----------|-----|
| **Costo** | Gratis | Pagado |
| **Velocidad** | Instantáneo | Segundos |
| **Desarrollo** | ✅ Ideal | ❌ No recomendado |
| **Testing** | ✅ Perfecto | ❌ Caro |
| **Producción** | ❌ No | ✅ Sí |

## 🔄 Flujo de Desarrollo

```
1. Desarrollo Local
   └─ LocalStack (gratis, rápido)

2. Testing
   └─ LocalStack (reproducible)

3. Staging
   └─ AWS (similar a producción)

4. Producción
   └─ AWS (real)
```

## ✅ Checklist RONDA 6

- [x] LocalStack en docker-compose
- [x] DynamoDB configurado
- [x] S3 configurado
- [x] Secrets Manager configurado
- [ ] ProductDynamoRepository implementado
- [ ] SecretsProvider implementado
- [ ] Tests de DynamoDB
- [ ] Tests de Secrets Manager
- [ ] Documentación completa

## 🎯 Próximos Pasos

1. Implementar ProductDynamoRepository
2. Implementar SecretsProvider
3. Agregar tests
4. Integrar en ProductHandler
5. Crear reportes con S3

---

**RONDA 6: LocalStack + DynamoDB + Secrets Manager** ✅
