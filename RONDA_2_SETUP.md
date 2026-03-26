# 🚀 RONDA 2 - Persistencia con MongoDB

## 📋 Objetivo
Reemplazar la BD en memoria con **MongoDB** para persistencia real.

## 🐳 Levantar MongoDB con Docker Compose

```bash
# Levantar servicios
docker-compose up -d

# Verificar que están corriendo
docker-compose ps

# Ver logs
docker-compose logs -f mongodb

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
docker-compose down

# Con volúmenes (borra datos)
docker-compose down -v
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

1. ✅ Agregar dependencia `spring-boot-starter-data-mongodb`
2. ✅ Crear `ProductMongoRepository` (Spring Data)
3. ✅ Actualizar `ProductRepositoryAdapter` para usar MongoDB
4. ✅ Actualizar `application.yaml` con configuración MongoDB
5. ✅ Compilar y probar

---

**Estado:** En progreso
