package com.empresa.dynamodb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import java.net.URI;

/**
 * RONDA 6: Configuración de AWS Secrets Manager
 * 
 * Esta clase configura el cliente de Secrets Manager para trabajar tanto con:
 * - LocalStack Secrets Manager (desarrollo local)
 * - AWS Secrets Manager (producción)
 * 
 * La configuración se activa según la propiedad 'aws.secretsmanager.local.enabled'
 * en application-local.yaml o application.yaml
 */
@Configuration
public class SecretsManagerConfig {
    
    /**
     * Bean para Secrets Manager local con LocalStack
     * 
     * Se activa cuando aws.secretsmanager.local.enabled=true
     * Conecta a http://localstack:4566 (endpoint de Secrets Manager en LocalStack)
     * Usa credenciales de prueba (test/test)
     * 
     * @return Cliente de Secrets Manager configurado para LocalStack
     */
    @Bean
    @ConditionalOnProperty(name = "aws.secretsmanager.local.enabled", havingValue = "true")
    public SecretsManagerClient localSecretsManagerClient() {
        return SecretsManagerClient.builder()
            // Endpoint de LocalStack para Secrets Manager
            .endpointOverride(URI.create("http://localstack:4566"))
            // Región (requerida aunque sea local)
            .region(Region.US_EAST_1)
            // Credenciales de prueba para LocalStack
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create("test", "test")
            ))
            .build();
    }
    
    /**
     * Bean para Secrets Manager en AWS
     * 
     * Se activa cuando aws.secretsmanager.local.enabled=false o no está definida
     * Usa las credenciales de AWS configuradas en el entorno
     * Conecta a Secrets Manager en AWS
     * 
     * @return Cliente de Secrets Manager configurado para AWS
     */
    @Bean
    @ConditionalOnProperty(name = "aws.secretsmanager.local.enabled", havingValue = "false", matchIfMissing = true)
    public SecretsManagerClient awsSecretsManagerClient() {
        return SecretsManagerClient.builder()
            // Región de AWS
            .region(Region.US_EAST_1)
            // Las credenciales se obtienen de variables de entorno o IAM role
            .build();
    }
}
