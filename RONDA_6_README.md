# RONDA 6: AWS Secrets Manager + CI/CD + Kubernetes

## 🎯 Objetivo
Completar la integración del proyecto con Kubernetes (Kind + Podman), preparar para AWS Secrets Manager y configurar CI/CD con GitHub Actions.

## ✅ Completado

### 6a: Docker Image
- ✅ Dockerfile multi-stage con amazoncorretto:17-alpine
- ✅ Imagen construida exitosamente: `arnold-products:latest`
- ✅ Tamaño optimizado con multi-stage build

### 6b: Kubernetes Cluster (Kind + Podman)
- ✅ Cluster creado: `arnold-cluster`
- ✅ Configurado para usar Podman como runtime
- ✅ Imagen cargada en el cluster

### 6c: Kubernetes Manifests
- ✅ Namespace: `arnold-products`
- ✅ ConfigMap: Variables de entorno centralizadas
- ✅ Deployment: arnold-products (2 replicas, health checks, resource limits)
- ✅ Services: ClusterIP + NodePort (puerto 30080)
- ✅ MongoDB Deployment + Service
- ✅ Redis Deployment + Service
- ✅ RabbitMQ Deployment + Service

## 📊 Estado Actual

```
PODS:
✅ arnold-products-6f7f4c4b55-6vbcq   1/1 Running
✅ arnold-products-6f7f4c4b55-vzvkw   1/1 Running
✅ mongodb-5648ff8fc7-bwhfk           1/1 Running
✅ rabbitmq-7cdf986b5c-bc95d          1/1 Running
✅ redis-7c89b64957-kz7p9             1/1 Running

SERVICES:
✅ arnold-products            ClusterIP:8080
✅ arnold-products-nodeport   NodePort:30080
✅ mongodb                    ClusterIP:27017
✅ rabbitmq                   ClusterIP:5672,15672
✅ redis                      ClusterIP:6379

APLICACIÓN:
✅ Health checks funcionando (readiness/liveness)
✅ Logs en tiempo real disponibles
✅ Replicas: 2 (rolling updates configurado)
```

## 🧪 Verificación

```bash
# Port forward (en terminal separada)
kubectl port-forward -n arnold-products svc/arnold-products-nodeport 8080:8080 --context kind-arnold-cluster

# Health check
curl http://localhost:8080/actuator/health

# Crear producto
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name":"K8s Product","price":999.0,"category":"Kubernetes"}'

# Listar productos
curl http://localhost:8080/products

# Ver logs
kubectl logs -n arnold-products -l app=arnold-products --context kind-arnold-cluster -f

# Ver estado de pods
kubectl get pods -n arnold-products --context kind-arnold-cluster -w
```

## 📝 Próximos Pasos

### RONDA 6d: AWS Secrets Manager
- [ ] Crear Secret en AWS Secrets Manager para credenciales
- [ ] Integrar AWS SDK en la aplicación
- [ ] Cargar secrets en tiempo de ejecución
- [ ] Usar IRSA (IAM Roles for Service Accounts) en EKS

### RONDA 6e: GitHub Actions CI/CD
- [ ] Crear workflow para build y push a ECR
- [ ] Crear workflow para deploy a EKS
- [ ] Configurar secrets en GitHub
- [ ] Implementar pruebas automatizadas

### RONDA 6f: Ingress y Load Balancing
- [ ] Crear Ingress para acceso externo
- [ ] Configurar HTTPS/TLS
- [ ] Implementar rate limiting

## 🏗️ Arquitectura Kubernetes

```
┌─────────────────────────────────────────────────────────┐
│                   Kind Cluster                          │
│  ┌──────────────────────────────────────────────────┐   │
│  │          arnold-products Namespace               │   │
│  │  ┌────────────────────────────────────────────┐  │   │
│  │  │  arnold-products Deployment (2 replicas)  │  │   │
│  │  │  ├─ Pod 1: arnold-products:latest         │  │   │
│  │  │  └─ Pod 2: arnold-products:latest         │  │   │
│  │  └────────────────────────────────────────────┘  │   │
│  │  ┌────────────────────────────────────────────┐  │   │
│  │  │  MongoDB Deployment                        │  │   │
│  │  │  └─ Pod: mongo:latest                      │  │   │
│  │  └────────────────────────────────────────────┘  │   │
│  │  ┌────────────────────────────────────────────┐  │   │
│  │  │  Redis Deployment                         │  │   │
│  │  │  └─ Pod: redis:7-alpine                   │  │   │
│  │  └────────────────────────────────────────────┘  │   │
│  │  ┌────────────────────────────────────────────┐  │   │
│  │  │  RabbitMQ Deployment                      │  │   │
│  │  │  └─ Pod: rabbitmq:3-management            │  │   │
│  │  └────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

## 📦 Comandos Útiles

```bash
# Ver estado del cluster
kubectl cluster-info --context kind-arnold-cluster

# Ver pods
kubectl get pods -n arnold-products --context kind-arnold-cluster

# Ver logs
kubectl logs -n arnold-products -l app=arnold-products --context kind-arnold-cluster

# Port forward
kubectl port-forward -n arnold-products svc/arnold-products-nodeport 8080:8080 --context kind-arnold-cluster

# Eliminar cluster
kind delete cluster --name arnold-cluster
```

## 🔐 Seguridad

- ✅ Containers corren como non-root (UID 1000)
- ✅ Resource limits configurados
- ✅ Health checks implementados
- ✅ Liveness y readiness probes
- ⏳ AWS Secrets Manager (próximo)
- ⏳ RBAC (próximo)
- ⏳ Network policies (próximo)

## 📊 Resumen de Progreso

```
RONDA 1: Clean Architecture        ✅ 100%
RONDA 2: MongoDB                   ✅ 100%
RONDA 3: Validaciones              ✅ 100%
RONDA 4: OpenAPI + Tests + Logging ✅ 100%
RONDA 5: RabbitMQ + Redis + Docker ✅ 100%
RONDA 6: Kubernetes + AWS + CI/CD  ✅ 60%
  6a: Docker Image                 ✅ 100%
  6b: Kind Cluster                 ✅ 100%
  6c: K8s Manifests                ✅ 100%
  6d: AWS Secrets Manager          ⏳ 0%
  6e: GitHub Actions CI/CD         ⏳ 0%
  6f: Ingress + TLS                ⏳ 0%
────────────────────────────────────────
TOTAL COMPLETADO:                  ✅ 85%
```
