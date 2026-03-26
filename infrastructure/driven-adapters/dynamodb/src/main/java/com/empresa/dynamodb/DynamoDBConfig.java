package com.empresa.dynamodb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

/**
 * RONDA 6: Configuración de DynamoDB
 * 
 * Esta clase configura el cliente de DynamoDB para trabajar tanto con:
 * - LocalStack (desarrollo local)
 * - AWS DynamoDB (producción)
 * 
 * La configuración se activa según la propiedad 'aws.dynamodb.local.enabled'
 * en application-local.yaml o application.yaml
 */
@Configuration
public class DynamoDBConfig {
    
    /**
     * Bean para DynamoDB local con LocalStack
     * 
     * Se activa cuando aws.dynamodb.local.enabled=true
     * Conecta a http://localstack:4571 (puerto DynamoDB en LocalStack)
     * Usa credenciales de prueba (test/test)
     * 
     * @return Cliente DynamoDB configurado para LocalStack
     */
    @Bean
    @ConditionalOnProperty(name = "aws.dynamodb.local.enabled", havingValue = "true")
    public DynamoDbClient localDynamoDbClient() {
        return DynamoDbClient.builder()
            // Endpoint de LocalStack (http://localstack:4571)
            .endpointOverride(URI.create("http://localstack:4571"))
            // Región (requerida aunque sea local)
            .region(Region.US_EAST_1)
            // Credenciales de prueba para LocalStack
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create("test", "test")
            ))
            .build();
    }
    
    /**
     * Bean para DynamoDB en AWS
     * 
     * Se activa cuando aws.dynamodb.local.enabled=false o no está definida
     * Usa las credenciales de AWS configuradas en el entorno
     * Conecta a DynamoDB en AWS
     * 
     * @return Cliente DynamoDB configurado para AWS
     */
    @Bean
    @ConditionalOnProperty(name = "aws.dynamodb.local.enabled", havingValue = "false", matchIfMissing = true)
    public DynamoDbClient awsDynamoDbClient() {
        return DynamoDbClient.builder()
            // Región de AWS
            .region(Region.US_EAST_1)
            // Las credenciales se obtienen de variables de entorno o IAM role
            .build();
    }
}
