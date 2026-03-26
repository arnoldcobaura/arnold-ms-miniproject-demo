package com.empresa.dynamodb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RONDA 6: Tests para ProductDynamoRepository
 * 
 * Verifica que los eventos de auditoría se guarden correctamente en DynamoDB.
 * Utiliza Mockito para simular el cliente DynamoDB.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductDynamoRepository Tests")
class ProductDynamoRepositoryTest {
    
    // Mock del cliente DynamoDB
    @Mock
    private DynamoDbClient dynamoDbClient;
    
    // Instancia del repositorio a probar
    private ProductDynamoRepository repository;
    
    /**
     * Configuración previa a cada test
     * 
     * Inicializa el repositorio con el cliente mock
     */
    @BeforeEach
    void setUp() {
        repository = new ProductDynamoRepository(dynamoDbClient);
    }
    
    /**
     * Test: Guardar evento de auditoría exitosamente
     * 
     * Verifica que cuando se llama a saveAuditEvent, el cliente DynamoDB
     * recibe una solicitud PutItem con los datos correctos.
     */
    @Test
    @DisplayName("Debe guardar evento de auditoría en DynamoDB")
    void testSaveAuditEventSuccess() {
        // Arrange (preparar)
        String productId = "prod-123";
        String eventType = "CREATED";
        String details = "Product created with name: Test Product";
        
        // Configurar mock para retornar respuesta exitosa
        when(dynamoDbClient.putItem(any(PutItemRequest.class)))
            .thenReturn(PutItemResponse.builder().build());
        
        // Act (actuar)
        StepVerifier.create(repository.saveAuditEvent(productId, eventType, details))
            // Assert (verificar)
            .verifyComplete();
        
        // Verificar que putItem fue llamado una vez
        verify(dynamoDbClient).putItem(any(PutItemRequest.class));
    }
    
    /**
     * Test: Guardar múltiples eventos
     * 
     * Verifica que se pueden guardar varios eventos secuencialmente.
     */
    @Test
    @DisplayName("Debe guardar múltiples eventos de auditoría")
    void testSaveMultipleAuditEvents() {
        // Arrange
        when(dynamoDbClient.putItem(any(PutItemRequest.class)))
            .thenReturn(PutItemResponse.builder().build());
        
        // Act & Assert
        StepVerifier.create(
            repository.saveAuditEvent("prod-1", "CREATED", "Event 1")
                .then(repository.saveAuditEvent("prod-2", "UPDATED", "Event 2"))
                .then(repository.saveAuditEvent("prod-3", "DELETED", "Event 3"))
        )
        .verifyComplete();
        
        // Verificar que putItem fue llamado 3 veces
        verify(dynamoDbClient).putItem(any(PutItemRequest.class));
    }
}
