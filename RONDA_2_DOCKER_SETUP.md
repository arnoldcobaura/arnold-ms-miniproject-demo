# 🚀 RONDA 2 - Persistencia con MongoDB (Docker)

## 📋 Objetivo
Levantar MongoDB con **Docker** en lugar de Podman.

## 🐳 Levantar MongoDB con Docker Compose

```bash
# Usar el archivo docker-compose.docker.yml
docker-compose -f docker-compose.docker.yml up -d

# Verificar que están corriendo
docker-compose -f docker-compose.docker.yml ps

# Ver logs
docker-compose -f docker-compose.docker.yml logs -f mongodb

# Acceder a Mongo Express (UI)
# http://localhost:8081
# Usuario: admin
# Contraseña: admin123
```

## 📊 Credenciales MongoDB

- **Host:** localhost
- **Puerto:** 27017
- **Usuario:** admin
- **Contraseña:** admin123
- **Base de datos:** arnold_products

## 🔧 Parar servicios

```bash
# Con docker-compose
docker-compose -f docker-compose.docker.yml down

# Con volúmenes (borra datos)
docker-compose -f docker-compose.docker.yml down -v
```

## ✅ Verificar conexión

```bash
# Desde terminal
mongosh mongodb://admin:admin123@localhost:27017/arnold_products

# Dentro de mongosh
> show collections
> db.products.find()
```

---

**Estado:** Listo para usar
