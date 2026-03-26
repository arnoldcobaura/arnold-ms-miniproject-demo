package com.empresa.dynamodb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

/**
 * RONDA 6: Proveedor de Secretos desde AWS Secrets Manager
 * 
 * Componente que gestiona la recuperación de secretos desde:
 * - AWS Secrets Manager (producción)
 * - LocalStack Secrets Manager (desarrollo)
 * 
 * Secretos soportados:
 * - arnold-products/mongodb: Credenciales de MongoDB
 * - arnold-products/rabbitmq: Credenciales de RabbitMQ
 * - arnold-products/redis: Configuración de Redis
 * 
 * Los secretos se almacenan como JSON strings y pueden ser parseados
 * por los componentes que los necesitan.
 */
@Component
@ConditionalOnProperty(name = "aws.secretsmanager.local.enabled", havingValue = "true")
public class SecretsProvider {
    
    // Logger para registrar operaciones
    private static final Logger logger = LoggerFactory.getLogger(SecretsProvider.class);
    
    // Cliente de Secrets Manager inyectado por Spring
    private final SecretsManagerClient secretsClient;
    
    /**
     * Constructor con inyección de dependencias
     * 
     * @param secretsClient Cliente de Secrets Manager configurado
     */
    public SecretsProvider(SecretsManagerClient secretsClient) {
        this.secretsClient = secretsClient;
    }
    
    /**
     * Recuperar un secreto de Secrets Manager
     * 
     * Obtiene el valor de un secreto almacenado en AWS Secrets Manager
     * o LocalStack (en desarrollo).
     * 
     * Ejemplos de uso:
     * - getSecret("arnold-products/mongodb")
     * - getSecret("arnold-products/rabbitmq")
     * - getSecret("arnold-products/redis")
     * 
     * @param secretName Nombre del secreto (ej: arnold-products/mongodb)
     * @return String con el valor del secreto (típicamente JSON)
     * @throws RuntimeException si hay error al recuperar el secreto
     */
    public String getSecret(String secretName) {
        try {
            logger.debug("Recuperando secreto: {}", secretName);
            
            // Crear request para obtener el secreto
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();
            
            // Ejecutar operación
            GetSecretValueResponse response = secretsClient.getSecretValue(request);
            
            // Obtener el valor del secreto
            String secretValue = response.secretString();
            
            // Log de éxito (sin mostrar el valor por seguridad)
            logger.info("Secreto recuperado exitosamente: {}", secretName);
            
            return secretValue;
            
        } catch (SecretsManagerException e) {
            // Log de error
            logger.error("Error al recuperar secreto: {}", secretName, e);
            
            // Lanzar excepción con mensaje descriptivo
            throw new RuntimeException(
                "Error al recuperar secreto '" + secretName + "' de Secrets Manager", e
            );
        }
    }
    
    /**
     * Recuperar un secreto con valor por defecto
     * 
     * Intenta recuperar un secreto, pero retorna un valor por defecto
     * si el secreto no existe o hay error.
     * 
     * Útil para configuraciones opcionales.
     * 
     * @param secretName Nombre del secreto
     * @param defaultValue Valor por defecto si no se encuentra el secreto
     * @return Valor del secreto o valor por defecto
     */
    public String getSecretOrDefault(String secretName, String defaultValue) {
        try {
            return getSecret(secretName);
        } catch (RuntimeException e) {
            logger.warn("Usando valor por defecto para secreto: {}", secretName);
            return defaultValue;
        }
    }
}
