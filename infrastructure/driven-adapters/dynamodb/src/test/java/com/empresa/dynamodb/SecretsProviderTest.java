package com.empresa.dynamodb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * RONDA 6: Tests para SecretsProvider
 * 
 * Verifica que los secretos se recuperen correctamente de Secrets Manager.
 * Utiliza Mockito para simular el cliente de Secrets Manager.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SecretsProvider Tests")
class SecretsProviderTest {
    
    // Mock del cliente de Secrets Manager
    @Mock
    private SecretsManagerClient secretsClient;
    
    // Instancia del proveedor a probar
    private SecretsProvider secretsProvider;
    
    /**
     * Configuración previa a cada test
     * 
     * Inicializa el proveedor con el cliente mock
     */
    @BeforeEach
    void setUp() {
        secretsProvider = new SecretsProvider(secretsClient);
    }
    
    /**
     * Test: Recuperar secreto exitosamente
     * 
     * Verifica que cuando se solicita un secreto, se retorna el valor correcto.
     */
    @Test
    @DisplayName("Debe recuperar secreto de Secrets Manager")
    void testGetSecretSuccess() {
        // Arrange (preparar)
        String secretName = "arnold-products/mongodb";
        String secretValue = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        
        // Configurar mock para retornar el secreto
        GetSecretValueResponse response = GetSecretValueResponse.builder()
            .secretString(secretValue)
            .build();
        
        when(secretsClient.getSecretValue(any(GetSecretValueRequest.class)))
            .thenReturn(response);
        
        // Act (actuar)
        String result = secretsProvider.getSecret(secretName);
        
        // Assert (verificar)
        assertNotNull(result);
        assertEquals(secretValue, result);
        assertTrue(result.contains("admin"));
    }
    
    /**
     * Test: Recuperar secreto con valor por defecto
     * 
     * Verifica que cuando hay error, se retorna el valor por defecto.
     */
    @Test
    @DisplayName("Debe retornar valor por defecto cuando hay error")
    void testGetSecretOrDefaultWithError() {
        // Arrange
        String secretName = "arnold-products/nonexistent";
        String defaultValue = "default-secret";
        
        // Configurar mock para lanzar excepción
        when(secretsClient.getSecretValue(any(GetSecretValueRequest.class)))
            .thenThrow(SecretsManagerException.builder()
                .message("Secret not found")
                .build());
        
        // Act
        String result = secretsProvider.getSecretOrDefault(secretName, defaultValue);
        
        // Assert
        assertEquals(defaultValue, result);
    }
    
    /**
     * Test: Recuperar múltiples secretos
     * 
     * Verifica que se pueden recuperar varios secretos diferentes.
     */
    @Test
    @DisplayName("Debe recuperar múltiples secretos")
    void testGetMultipleSecrets() {
        // Arrange
        String mongoSecret = "{\"uri\":\"mongodb://admin:admin123@localhost:27017\"}";
        String rabbitSecret = "{\"host\":\"rabbitmq\",\"port\":5672}";
        
        // Configurar mock para retornar diferentes secretos
        when(secretsClient.getSecretValue(any(GetSecretValueRequest.class)))
            .thenReturn(GetSecretValueResponse.builder().secretString(mongoSecret).build())
            .thenReturn(GetSecretValueResponse.builder().secretString(rabbitSecret).build());
        
        // Act
        String mongo = secretsProvider.getSecret("arnold-products/mongodb");
        String rabbit = secretsProvider.getSecret("arnold-products/rabbitmq");
        
        // Assert
        assertEquals(mongoSecret, mongo);
        assertEquals(rabbitSecret, rabbit);
        assertTrue(mongo.contains("mongodb"));
        assertTrue(rabbit.contains("rabbitmq"));
    }
}
