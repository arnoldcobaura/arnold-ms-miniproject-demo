# 🚀 RONDA 2 - Persistencia con MongoDB (Podman)

## 📋 Objetivo
Levantar MongoDB con **Podman** en lugar de Docker.

## 🐳 Levantar MongoDB con Podman Compose

### Opción 1: Usar podman-compose (recomendado)

```bash
# Instalar podman-compose si no lo tienes
brew install podman-compose

# Levantar servicios
podman-compose up -d

# Verificar que están corriendo
podman-compose ps

# Ver logs
podman-compose logs -f mongodb

# Acceder a Mongo Express (UI)
# http://localhost:8081
# Usuario: admin
# Contraseña: admin123
```

### Opción 2: Usar podman directamente

```bash
# Crear red
podman network create arnold-network

# Levantar MongoDB
podman run -d \
  --name arnold-mongodb \
  --network arnold-network \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=admin123 \
  -e MONGO_INITDB_DATABASE=arnold_products \
  -p 27017:27017 \
  -v mongodb_data:/data/db \
  mongo:7.0

# Levantar Mongo Express
podman run -d \
  --name arnold-mongo-express \
  --network arnold-network \
  -e ME_CONFIG_MONGODB_ADMINUSERNAME=admin \
  -e ME_CONFIG_MONGODB_ADMINPASSWORD=admin123 \
  -e ME_CONFIG_MONGODB_URL=mongodb://admin:admin123@arnold-mongodb:27017/ \
  -p 8081:8081 \
  mongo-express:latest
```

## 📊 Credenciales MongoDB

- **Host:** localhost
- **Puerto:** 27017
- **Usuario:** admin
- **Contraseña:** admin123
- **Base de datos:** arnold_products

## 🔧 Parar servicios

```bash
# Con podman-compose
podman-compose down

# Con podman directo
podman stop arnold-mongodb arnold-mongo-express
podman rm arnold-mongodb arnold-mongo-express
podman network rm arnold-network
```

## ✅ Verificar conexión

```bash
# Desde terminal
mongosh mongodb://admin:admin123@localhost:27017/arnold_products

# Dentro de mongosh
> show collections
> db.products.find()
```

## 📝 Cambios en el código

1. ✅ Agregar dependencia `spring-boot-starter-data-mongodb-reactive`
2. ✅ Crear `ProductMongoRepository` (Spring Data)
3. ✅ Actualizar `ProductRepositoryAdapter` para usar MongoDB
4. ✅ Actualizar `application.yaml` con configuración MongoDB
5. ⏳ Compilar y probar

---

**Estado:** En progreso
