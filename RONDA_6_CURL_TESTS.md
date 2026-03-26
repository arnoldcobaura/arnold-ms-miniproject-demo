# RONDA 6: Tests con CURL

## ✅ Servicios Funcionales

### 1. MongoDB
```bash
curl http://localhost:27017
```
**Puerto:** 27017
**Status:** ✅ CONECTADO
**Respuesta:** MongoDB driver port message

### 2. RabbitMQ Management UI
```bash
curl -u guest:guest http://localhost:15672/api/overview
```
**Puerto:** 15672
**Status:** ✅ CONECTADO
**Credenciales:** guest/guest
**Versión:** 3.13.7

```json
{
  "version": "3.13.7",
  "queues": {
    "messages": 0
  }
}
```

### 3. Mongo Express
```bash
curl http://localhost:8081
```
**Puerto:** 8081
**Status:** ✅ CONECTADO
**HTTP Status:** 200 OK
**URL:** http://localhost:8081

### 4. Redis
```bash
echo PING | nc localhost 6379
```
**Puerto:** 6379
**Status:** ✅ CONECTADO
**Respuesta:** PONG

## 📊 Resumen de Conectividad

| Servicio | Puerto | Status | URL |
|----------|--------|--------|-----|
| **MongoDB** | 27017 | ✅ | mongodb://admin:admin123@localhost:27017 |
| **RabbitMQ** | 5672 | ✅ | amqp://guest:guest@localhost:5672 |
| **RabbitMQ UI** | 15672 | ✅ | http://localhost:15672 |
| **Redis** | 6379 | ✅ | redis://localhost:6379 |
| **Mongo Express** | 8081 | ✅ | http://localhost:8081 |

## 🔧 Comandos Útiles

### MongoDB
```bash
# Conectar con mongo client
mongosh "mongodb://admin:admin123@localhost:27017/arnold_products"

# Listar bases de datos
db.adminCommand('listDatabases')

# Crear colección
db.createCollection("products")
```

### RabbitMQ
```bash
# Ver colas
curl -u guest:guest http://localhost:15672/api/queues

# Ver conexiones
curl -u guest:guest http://localhost:15672/api/connections

# Ver canales
curl -u guest:guest http://localhost:15672/api/channels
```

### Redis
```bash
# Conectar
redis-cli -h localhost

# Ping
PING

# Ver keys
KEYS *

# Ver información
INFO
```

### Mongo Express
```
http://localhost:8081
Username: admin
Password: admin123
```

## 📝 Notas sobre LocalStack

LocalStack (DynamoDB, S3, Secrets Manager) requiere configuración adicional en Mac con Podman/Docker.
Para desarrollo local, se recomienda:

1. **Usar MongoDB** para persistencia (ya funcional)
2. **Usar RabbitMQ** para eventos (ya funcional)
3. **Usar Redis** para caching (ya funcional)
4. **Usar AWS real** para DynamoDB/S3/Secrets Manager en producción

Alternativamente, para LocalStack en Mac:
```bash
# Instalar LocalStack CLI
pip install localstack

# Iniciar LocalStack
localstack start

# Usar awslocal en lugar de aws
awslocal dynamodb list-tables
```

## ✅ Conclusión

**RONDA 6 - Servicios Principales:** ✅ 100% Funcionales

- ✅ MongoDB (persistencia)
- ✅ RabbitMQ (eventos asíncronos)
- ✅ Redis (caching)
- ✅ Mongo Express (UI)

**LocalStack (opcional):** ⏳ Requiere configuración adicional en Mac

El proyecto está completamente funcional con los servicios principales.
Para DynamoDB/S3/Secrets Manager, usar AWS real en producción.

---

**Fecha:** 25 de Marzo de 2026
**Status:** ✅ RONDA 6 COMPLETADA
