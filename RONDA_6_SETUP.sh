#!/bin/bash

# RONDA 6: Setup Script para LocalStack + DynamoDB + Secrets Manager
# Este script configura todos los recursos necesarios en LocalStack

set -e

echo "🚀 RONDA 6: Configurando LocalStack..."

# Colores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Esperar a que LocalStack esté listo
echo -e "${BLUE}⏳ Esperando a que LocalStack esté listo...${NC}"
sleep 10

# Verificar que LocalStack está disponible
echo -e "${BLUE}🔍 Verificando LocalStack...${NC}"
if ! curl -s http://localhost:4566/_localstack/health > /dev/null; then
    echo "❌ LocalStack no está disponible en http://localhost:4566"
    exit 1
fi
echo -e "${GREEN}✅ LocalStack está listo${NC}"

# Crear tabla DynamoDB
echo -e "${BLUE}📊 Creando tabla DynamoDB...${NC}"
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
  --region us-east-1 2>/dev/null || echo "Tabla ya existe"
echo -e "${GREEN}✅ Tabla DynamoDB creada${NC}"

# Crear tabla de eventos
echo -e "${BLUE}📊 Creando tabla de eventos...${NC}"
aws dynamodb create-table \
  --table-name arnold-products-events \
  --attribute-definitions \
    AttributeName=eventId,AttributeType=S \
    AttributeName=timestamp,AttributeType=N \
  --key-schema \
    AttributeName=eventId,KeyType=HASH \
    AttributeName=timestamp,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 2>/dev/null || echo "Tabla ya existe"
echo -e "${GREEN}✅ Tabla de eventos creada${NC}"

# Crear secreto para MongoDB
echo -e "${BLUE}🔐 Creando secreto de MongoDB...${NC}"
aws secretsmanager create-secret \
  --name arnold-products/mongodb \
  --description "MongoDB credentials" \
  --secret-string '{
    "username": "admin",
    "password": "admin123",
    "uri": "mongodb://admin:admin123@mongodb:27017/arnold_products"
  }' \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 2>/dev/null || echo "Secreto ya existe"
echo -e "${GREEN}✅ Secreto de MongoDB creado${NC}"

# Crear secreto para RabbitMQ
echo -e "${BLUE}🔐 Creando secreto de RabbitMQ...${NC}"
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
  --region us-east-1 2>/dev/null || echo "Secreto ya existe"
echo -e "${GREEN}✅ Secreto de RabbitMQ creado${NC}"

# Crear secreto para Redis
echo -e "${BLUE}🔐 Creando secreto de Redis...${NC}"
aws secretsmanager create-secret \
  --name arnold-products/redis \
  --description "Redis configuration" \
  --secret-string '{
    "host": "redis",
    "port": 6379
  }' \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 2>/dev/null || echo "Secreto ya existe"
echo -e "${GREEN}✅ Secreto de Redis creado${NC}"

# Crear bucket S3
echo -e "${BLUE}📦 Creando bucket S3...${NC}"
aws s3 mb s3://arnold-products-reports \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 2>/dev/null || echo "Bucket ya existe"
echo -e "${GREEN}✅ Bucket S3 creado${NC}"

# Resumen
echo ""
echo -e "${GREEN}════════════════════════════════════════${NC}"
echo -e "${GREEN}✅ RONDA 6: Setup completado${NC}"
echo -e "${GREEN}════════════════════════════════════════${NC}"
echo ""
echo "📊 Recursos creados:"
echo "  • Tabla DynamoDB: arnold-products-audit"
echo "  • Tabla DynamoDB: arnold-products-events"
echo "  • Secreto: arnold-products/mongodb"
echo "  • Secreto: arnold-products/rabbitmq"
echo "  • Secreto: arnold-products/redis"
echo "  • Bucket S3: arnold-products-reports"
echo ""
echo "🌐 LocalStack Web UI: http://localhost:9000"
echo "🔗 Endpoint: http://localhost:4566"
echo ""
