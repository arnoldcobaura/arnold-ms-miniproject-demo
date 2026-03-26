# RONDA 6d: AWS Secrets Manager Integration Guide

## 🔐 Configuración de AWS Secrets Manager

### 1. Crear Secret en AWS
```bash
aws secretsmanager create-secret \
  --name arnold-products/mongodb \
  --description "MongoDB credentials for arnold-products" \
  --secret-string '{
    "username": "admin",
    "password": "admin123",
    "uri": "mongodb://admin:admin123@mongodb.arnold-products.svc.cluster.local:27017/arnold_products"
  }' \
  --region us-east-1

aws secretsmanager create-secret \
  --name arnold-products/rabbitmq \
  --description "RabbitMQ credentials for arnold-products" \
  --secret-string '{
    "username": "guest",
    "password": "guest",
    "host": "rabbitmq.arnold-products.svc.cluster.local",
    "port": 5672
  }' \
  --region us-east-1
```

### 2. Crear IAM Role para EKS (IRSA)
```bash
# Crear trust policy
cat > trust-policy.json << EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::ACCOUNT_ID:oidc-provider/oidc.eks.us-east-1.amazonaws.com/id/EXAMPLED539D4633E53DE1B716D3041E"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "oidc.eks.us-east-1.amazonaws.com/id/EXAMPLED539D4633E53DE1B716D3041E:sub": "system:serviceaccount:arnold-products:arnold-products-sa"
        }
      }
    }
  ]
}
EOF

# Crear role
aws iam create-role \
  --role-name arnold-products-role \
  --assume-role-policy-document file://trust-policy.json

# Crear policy para acceder a Secrets Manager
cat > secrets-policy.json << EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "secretsmanager:GetSecretValue",
        "secretsmanager:DescribeSecret"
      ],
      "Resource": "arn:aws:secretsmanager:us-east-1:ACCOUNT_ID:secret:arnold-products/*"
    }
  ]
}
EOF

# Attach policy
aws iam put-role-policy \
  --role-name arnold-products-role \
  --policy-name arnold-products-secrets-policy \
  --policy-document file://secrets-policy.json
```

### 3. Actualizar Deployment para usar ServiceAccount
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: arnold-products
  namespace: arnold-products
spec:
  template:
    spec:
      serviceAccountName: arnold-products-sa
      containers:
      - name: arnold-products
        image: localhost/arnold-products:latest
        env:
        - name: AWS_ROLE_ARN
          value: arn:aws:iam::ACCOUNT_ID:role/arnold-products-role
        - name: AWS_WEB_IDENTITY_TOKEN_FILE
          value: /var/run/secrets/eks.amazonaws.com/serviceaccount/token
```

### 4. Integración en Spring Boot

#### Agregar dependencia
```gradle
implementation 'software.amazon.awssdk:secretsmanager:2.20.0'
```

#### Crear SecretsManagerConfig
```java
@Configuration
public class SecretsManagerConfig {
    
    @Bean
    public SecretsManagerClient secretsManagerClient() {
        return SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .build();
    }
    
    @Bean
    public SecretsProvider secretsProvider(SecretsManagerClient client) {
        return new SecretsProvider(client);
    }
}
```

#### Crear SecretsProvider
```java
@Component
public class SecretsProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(SecretsProvider.class);
    private final SecretsManagerClient secretsClient;
    
    public SecretsProvider(SecretsManagerClient secretsClient) {
        this.secretsClient = secretsClient;
    }
    
    public String getSecret(String secretName) {
        try {
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();
            
            GetSecretValueResponse response = secretsClient.getSecretValue(request);
            logger.info("Secret retrieved: {}", secretName);
            return response.secretString();
        } catch (Exception e) {
            logger.error("Error retrieving secret: {}", secretName, e);
            throw new RuntimeException("Failed to retrieve secret: " + secretName, e);
        }
    }
}
```

#### Usar en Properties
```java
@Configuration
@EnableConfigurationProperties
public class MongoDBProperties {
    
    private final SecretsProvider secretsProvider;
    
    @Value("${mongodb.uri:}")
    private String mongodbUri;
    
    public MongoDBProperties(SecretsProvider secretsProvider) {
        this.secretsProvider = secretsProvider;
    }
    
    @Bean
    public String mongodbConnectionUri() {
        if (mongodbUri != null && !mongodbUri.isEmpty()) {
            return mongodbUri;
        }
        
        // Obtener de AWS Secrets Manager
        String secretJson = secretsProvider.getSecret("arnold-products/mongodb");
        // Parsear JSON y extraer URI
        return extractUriFromSecret(secretJson);
    }
    
    private String extractUriFromSecret(String secretJson) {
        // Implementar parsing de JSON
        return secretJson;
    }
}
```

## 🔄 Alternativas a AWS Secrets Manager

### Sealed Secrets (para Kind/Local)
```bash
# Instalar Sealed Secrets controller
kubectl apply -f https://github.com/bitnami-labs/sealed-secrets/releases/download/v0.18.0/controller.yaml

# Crear secret
echo -n mypassword | kubectl create secret generic mysecret \
  --dry-run=client \
  --from-file=password=/dev/stdin \
  -o yaml | kubeseal -f - > mysealedsecret.yaml

# Aplicar
kubectl apply -f mysealedsecret.yaml
```

### HashiCorp Vault
```bash
# Instalar Vault Helm chart
helm repo add hashicorp https://helm.releases.hashicorp.com
helm install vault hashicorp/vault -n vault --create-namespace

# Configurar auth y secrets
vault auth enable kubernetes
vault write auth/kubernetes/config \
  token_reviewer_jwt=@/var/run/secrets/kubernetes.io/serviceaccount/token \
  kubernetes_host=https://$KUBERNETES_SERVICE_HOST:$KUBERNETES_SERVICE_PORT \
  kubernetes_ca_cert=@/var/run/secrets/kubernetes.io/serviceaccount/ca.crt
```

## 📋 Checklist de Seguridad

- [ ] Secrets no están en código fuente
- [ ] RBAC configurado correctamente
- [ ] IRSA habilitado en EKS
- [ ] Secrets Manager encriptado con KMS
- [ ] Audit logging habilitado
- [ ] Network policies configuradas
- [ ] Pod security policies aplicadas
- [ ] Secrets rotados regularmente

## 🔗 Referencias

- [AWS Secrets Manager Documentation](https://docs.aws.amazon.com/secretsmanager/)
- [EKS IRSA Documentation](https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts.html)
- [Spring Cloud AWS](https://spring.io/projects/spring-cloud-aws)
- [Sealed Secrets](https://github.com/bitnami-labs/sealed-secrets)
