# RONDA 6: Guía Completa de Kubernetes con Kind + Podman

## 🚀 Inicio Rápido

### 1. Crear el Cluster
```bash
export KIND_EXPERIMENTAL_PROVIDER=podman
kind create cluster --name arnold-cluster
```

### 2. Construir la Imagen
```bash
podman build -t localhost/arnold-products:latest -f Dockerfile .
```

### 3. Cargar Imagen en el Cluster
```bash
export KIND_EXPERIMENTAL_PROVIDER=podman
kind load docker-image localhost/arnold-products:latest --name arnold-cluster
```

### 4. Aplicar Manifiestos
```bash
kubectl apply -f kubernetes/namespace.yaml
kubectl apply -f kubernetes/ --context kind-arnold-cluster
```

### 5. Verificar Deployment
```bash
kubectl get pods -n arnold-products --context kind-arnold-cluster
kubectl get svc -n arnold-products --context kind-arnold-cluster
```

### 6. Port Forward
```bash
kubectl port-forward -n arnold-products svc/arnold-products-nodeport 8080:8080 --context kind-arnold-cluster
```

### 7. Testear API
```bash
# Health check
curl http://localhost:8080/actuator/health

# Crear producto
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Product","price":100.0,"category":"Test"}'

# Listar productos
curl http://localhost:8080/products

# Swagger UI
open http://localhost:8080/swagger-ui.html
```

## 📊 Estructura de Manifiestos

```
kubernetes/
├── namespace.yaml              # Namespace arnold-products
├── configmap.yaml              # Variables de entorno
├── deployment.yaml             # arnold-products (2 replicas)
├── service.yaml                # ClusterIP + NodePort
├── mongodb-deployment.yaml      # MongoDB + Service
├── redis-deployment.yaml        # Redis + Service
└── rabbitmq-deployment.yaml     # RabbitMQ + Service
```

## 🔍 Debugging

```bash
# Ver logs de un pod específico
kubectl logs -n arnold-products <pod-name> --context kind-arnold-cluster

# Ver logs en tiempo real
kubectl logs -n arnold-products -l app=arnold-products --context kind-arnold-cluster -f

# Ejecutar comando en un pod
kubectl exec -it -n arnold-products <pod-name> -- /bin/sh

# Describir un pod (problemas)
kubectl describe pod -n arnold-products <pod-name> --context kind-arnold-cluster

# Ver eventos del cluster
kubectl get events -n arnold-products --context kind-arnold-cluster
```

## 🧹 Limpieza

```bash
# Eliminar namespace (borra todo)
kubectl delete namespace arnold-products --context kind-arnold-cluster

# Eliminar cluster
kind delete cluster --name arnold-cluster

# Limpiar imágenes de Podman
podman rmi localhost/arnold-products:latest
```

## 📈 Escalado

```bash
# Escalar deployment
kubectl scale deployment arnold-products -n arnold-products --replicas=3 --context kind-arnold-cluster

# Ver replicas
kubectl get deployment arnold-products -n arnold-products --context kind-arnold-cluster
```

## 🔄 Rolling Updates

```bash
# Actualizar imagen
kubectl set image deployment/arnold-products arnold-products=localhost/arnold-products:v2 \
  -n arnold-products --context kind-arnold-cluster

# Ver estado del rollout
kubectl rollout status deployment/arnold-products -n arnold-products --context kind-arnold-cluster

# Revertir cambios
kubectl rollout undo deployment/arnold-products -n arnold-products --context kind-arnold-cluster
```

## 📝 Variables de Entorno (ConfigMap)

```yaml
MONGODB_URI: mongodb://admin:admin123@mongodb:27017/arnold_products
RABBITMQ_HOST: rabbitmq
RABBITMQ_PORT: 5672
RABBITMQ_USER: guest
REDIS_HOST: redis
REDIS_PORT: 6379
SPRING_PROFILES_ACTIVE: kubernetes
LOGGING_LEVEL_ROOT: INFO
LOGGING_LEVEL_COM_EMPRESA: DEBUG
```

## 🔐 Próximos Pasos

### AWS Secrets Manager Integration
1. Crear secrets en AWS
2. Usar IRSA (IAM Roles for Service Accounts)
3. Montar secrets como volúmenes

### CI/CD con GitHub Actions
1. Build y push a ECR
2. Deploy automático a EKS
3. Pruebas automatizadas

### Ingress y Load Balancing
1. Configurar Ingress controller
2. HTTPS/TLS
3. Rate limiting

## 📚 Recursos

- [Kind Documentation](https://kind.sigs.k8s.io/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Spring Boot on Kubernetes](https://spring.io/guides/gs/spring-boot-kubernetes/)
