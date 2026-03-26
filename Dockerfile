# RONDA 6: Dockerfile para Spring Boot 3 + WebFlux
# Multi-stage build para optimizar tamaño de imagen

# Stage 1: Build
FROM amazoncorretto:17-alpine AS builder

WORKDIR /build

# Copiar gradle wrapper y archivos de configuración
COPY gradle gradle
COPY gradlew .
COPY settings.gradle .
COPY build.gradle .

# Copiar código fuente
COPY domain domain
COPY infrastructure infrastructure
COPY applications applications

# Compilar aplicación
RUN chmod +x gradlew && \
    ./gradlew clean build -x test --no-daemon

# Stage 2: Runtime
FROM amazoncorretto:17-alpine

WORKDIR /app

# Copiar JAR desde stage anterior
COPY --from=builder /build/applications/app-service/build/libs/app-service-*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
