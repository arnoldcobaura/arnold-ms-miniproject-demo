# Security Policy

## Credenciales de Desarrollo

Las credenciales en `docker-compose-ronda5.yml` son **solo para desarrollo local**:

```yaml
MongoDB:     admin / admin123
RabbitMQ:    guest / guest
AWS (Local): test / test
```

Estas son credenciales estándar de prueba y **NO deben usarse en producción**.

## Producción

Para producción, usar:

### 1. AWS Secrets Manager
```java
@Component
public class SecretsProvider {
    private final SecretsManagerClient secretsClient;
    
    public String getSecret(String secretName) {
        // Recupera de AWS Secrets Manager real
        return secretsClient.getSecretValue(request).secretString();
    }
}
```

### 2. Variables de Entorno
```bash
export MONGODB_URI=mongodb://user:pass@host:27017/db
export RABBITMQ_HOST=rabbitmq.example.com
export REDIS_HOST=redis.example.com
export AWS_REGION=us-east-1
```

### 3. IAM Roles (Kubernetes)
```yaml
serviceAccountName: arnold-products
# Usa IRSA para acceso a AWS sin credenciales hardcodeadas
```

## Escaneo de Seguridad

El proyecto incluye:

- **Trivy** - Escaneo de vulnerabilidades en imágenes Docker
- **OWASP Dependency-Check** - Análisis de dependencias vulnerables
- **SonarQube** - Análisis de código estático

## Recomendaciones

1. ✅ Usar AWS Secrets Manager en producción
2. ✅ Usar IAM roles en Kubernetes (IRSA)
3. ✅ Habilitar HTTPS/TLS
4. ✅ Usar network policies en Kubernetes
5. ✅ Implementar rate limiting
6. ✅ Usar WAF en API Gateway
7. ✅ Auditar accesos a DynamoDB
8. ✅ Encriptar datos en tránsito y en reposo

## Reportar Vulnerabilidades

Si encuentras una vulnerabilidad, por favor reportarla a través de GitHub Security Advisories.

---

**Última actualización:** 25 de Marzo de 2026
