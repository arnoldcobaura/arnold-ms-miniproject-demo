# RONDA 6e: GitHub Actions CI/CD Guide

## 🚀 Workflows Configurados

### 1. Build and Deploy Workflow
**Archivo:** `.github/workflows/build-and-deploy.yml`

**Triggers:**
- Push a `main` o `develop`
- Pull requests a `main` o `develop`

**Pasos:**
1. ✅ Checkout código
2. ✅ Setup JDK 17
3. ✅ Build con Gradle
4. ✅ Ejecutar tests
5. ✅ Build imagen Docker
6. ✅ Push a GitHub Container Registry (GHCR)
7. ✅ Deploy a EKS (solo en main)

### 2. Security Scan Workflow
**Archivo:** `.github/workflows/security-scan.yml`

**Triggers:**
- Push a `main` o `develop`
- Pull requests a `main` o `develop`
- Scheduled: Domingos a las 2 AM UTC

**Scans:**
- Trivy: Vulnerabilidades en dependencias
- OWASP Dependency-Check: Análisis de dependencias
- GitHub CodeQL: Análisis de código

## 📋 Configuración Requerida

### 1. GitHub Secrets
Agregar en Settings > Secrets and variables > Actions:

```
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
DOCKER_USERNAME
DOCKER_PASSWORD
```

### 2. GitHub Container Registry (GHCR)
```bash
# Login
echo ${{ secrets.GITHUB_TOKEN }} | docker login ghcr.io -u ${{ github.actor }} --password-stdin

# Tag imagen
docker tag arnold-products:latest ghcr.io/arnoldcobaura/arnold-ms-miniproject-demo:latest

# Push
docker push ghcr.io/arnoldcobaura/arnold-ms-miniproject-demo:latest
```

### 3. AWS Credentials
```bash
# Crear usuario IAM con permisos para EKS
aws iam create-user --user-name github-actions

# Attach policy
aws iam attach-user-policy \
  --user-name github-actions \
  --policy-arn arn:aws:iam::aws:policy/AmazonEKSFullAccess

# Crear access keys
aws iam create-access-key --user-name github-actions
```

## 📊 Pipeline Stages

```
┌─────────────────────────────────────────────────────┐
│                  GitHub Actions                     │
├─────────────────────────────────────────────────────┤
│                                                     │
│  1. Checkout Code                                   │
│     └─ git checkout                                 │
│                                                     │
│  2. Setup Environment                               │
│     ├─ JDK 17                                       │
│     ├─ Docker Buildx                                │
│     └─ AWS Credentials                              │
│                                                     │
│  3. Build & Test                                    │
│     ├─ Gradle clean build                           │
│     ├─ Run tests                                    │
│     └─ Security scans                               │
│                                                     │
│  4. Build Docker Image                              │
│     ├─ Multi-stage build                            │
│     ├─ Tag image                                    │
│     └─ Push to GHCR                                 │
│                                                     │
│  5. Deploy (main only)                              │
│     ├─ Configure AWS credentials                    │
│     ├─ Update kubeconfig                            │
│     ├─ Apply manifests                              │
│     └─ Verify deployment                            │
│                                                     │
└─────────────────────────────────────────────────────┘
```

## 🔍 Monitoreo de Workflows

### Ver status en GitHub
```
Settings > Actions > All workflows
```

### Ver logs localmente
```bash
# Instalar GitHub CLI
brew install gh

# Login
gh auth login

# Ver workflows
gh workflow list

# Ver runs
gh run list

# Ver logs de un run
gh run view <run-id> --log
```

## 🐛 Debugging

### Habilitar debug logging
```yaml
env:
  ACTIONS_STEP_DEBUG: true
```

### Usar tmate para SSH en runner
```yaml
- name: Setup tmate session
  uses: mxschmitt/action-tmate@v3
```

## 📈 Mejoras Futuras

- [ ] Implementar SonarQube para análisis de código
- [ ] Agregar pruebas de integración
- [ ] Implementar canary deployments
- [ ] Agregar notificaciones a Slack
- [ ] Implementar rollback automático
- [ ] Agregar performance testing
- [ ] Implementar blue-green deployments

## 🔗 Recursos

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [GitHub Container Registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry)
- [AWS EKS Documentation](https://docs.aws.amazon.com/eks/)
- [Docker Build Action](https://github.com/docker/build-push-action)
