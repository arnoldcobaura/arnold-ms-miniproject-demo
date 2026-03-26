package com.empresa.dynamodb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * RONDA 6: Repositorio de DynamoDB para Productos
 * 
 * Proporciona operaciones de auditoría y análisis histórico en DynamoDB.
 * Guarda eventos de productos para:
 * - Auditoría (quién, qué, cuándo)
 * - Análisis histórico
 * - Reportes
 * - Cumplimiento normativo
 * 
 * Tabla: arnold-products-audit
 * Clave primaria: productId (HASH) + timestamp (RANGE)
 */
@Repository
public class ProductDynamoRepository {
    
    // Logger para registrar operaciones
    private static final Logger logger = LoggerFactory.getLogger(ProductDynamoRepository.class);
    
    // Nombre de la tabla DynamoDB para auditoría
    private static final String TABLE_NAME = "arnold-products-audit";
    
    // Cliente DynamoDB inyectado por Spring
    private final DynamoDbClient dynamoDbClient;
    
    /**
     * Constructor con inyección de dependencias
     * 
     * @param dynamoDbClient Cliente DynamoDB configurado en DynamoDBConfig
     */
    public ProductDynamoRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }
    
    /**
     * Guardar evento de producto en DynamoDB para auditoría
     * 
     * Operación reactiva que guarda un evento en la tabla de auditoría.
     * Útil para registrar:
     * - Creación de productos
     * - Actualizaciones
     * - Eliminaciones
     * - Cambios de estado
     * 
     * @param productId ID del producto (clave HASH)
     * @param eventType Tipo de evento (CREATED, UPDATED, DELETED, etc.)
     * @param details Detalles adicionales del evento (JSON)
     * @return Mono<Void> que completa cuando se guarda el evento
     */
    public Mono<Void> saveAuditEvent(String productId, String eventType, String details) {
        return Mono.fromRunnable(() -> {
            try {
                // Crear mapa de atributos para el item de DynamoDB
                Map<String, AttributeValue> item = new HashMap<>();
                
                // Clave HASH: productId
                item.put("productId", AttributeValue.builder().s(productId).build());
                
                // Tipo de evento
                item.put("eventType", AttributeValue.builder().s(eventType).build());
                
                // Clave RANGE: timestamp (milisegundos desde epoch)
                // Permite ordenar eventos cronológicamente
                item.put("timestamp", AttributeValue.builder()
                    .n(String.valueOf(System.currentTimeMillis()))
                    .build());
                
                // Detalles del evento (JSON string)
                item.put("details", AttributeValue.builder().s(details).build());
                
                // Crear request de PutItem
                PutItemRequest request = PutItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .item(item)
                    .build();
                
                // Ejecutar operación
                dynamoDbClient.putItem(request);
                
                // Log de éxito
                logger.info("Evento de auditoría guardado: productId={}, eventType={}, timestamp={}",
                    productId, eventType, System.currentTimeMillis());
                
            } catch (DynamoDbException e) {
                // Log de error
                logger.error("Error al guardar evento de auditoría en DynamoDB: productId={}, eventType={}",
                    productId, eventType, e);
                // Re-lanzar excepción para que sea manejada por el caller
                throw new RuntimeException("Error al guardar auditoría en DynamoDB", e);
            }
        });
    }
    
    /**
     * Crear tabla DynamoDB si no existe
     * 
     * Operación que verifica si la tabla existe y la crea si es necesario.
     * Se ejecuta una sola vez al iniciar la aplicación.
     * 
     * Estructura de la tabla:
     * - productId (String, HASH key)
     * - timestamp (Number, RANGE key)
     * - eventType (String)
     * - details (String)
     * 
     * Billing: PAY_PER_REQUEST (pago por uso, ideal para desarrollo)
     */
    public void createTableIfNotExists() {
        try {
            // Listar tablas existentes
            ListTablesRequest listTablesRequest = ListTablesRequest.builder().build();
            ListTablesResponse listTablesResponse = dynamoDbClient.listTables(listTablesRequest);
            
            // Verificar si la tabla ya existe
            if (!listTablesResponse.tableNames().contains(TABLE_NAME)) {
                logger.info("Tabla {} no existe, creando...", TABLE_NAME);
                
                // Crear tabla
                CreateTableRequest createTableRequest = CreateTableRequest.builder()
                    .tableName(TABLE_NAME)
                    // Definir esquema de claves
                    .keySchema(
                        // Clave HASH (partición)
                        KeySchemaElement.builder()
                            .attributeName("productId")
                            .keyType(KeyType.HASH)
                            .build(),
                        // Clave RANGE (ordenamiento)
                        KeySchemaElement.builder()
                            .attributeName("timestamp")
                            .keyType(KeyType.RANGE)
                            .build()
                    )
                    // Definir tipos de atributos
                    .attributeDefinitions(
                        // productId es String
                        AttributeDefinition.builder()
                            .attributeName("productId")
                            .attributeType(ScalarAttributeType.S)
                            .build(),
                        // timestamp es Number
                        AttributeDefinition.builder()
                            .attributeName("timestamp")
                            .attributeType(ScalarAttributeType.N)
                            .build()
                    )
                    // Modo de facturación: pago por uso
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build();
                
                // Ejecutar creación
                dynamoDbClient.createTable(createTableRequest);
                logger.info("Tabla DynamoDB creada exitosamente: {}", TABLE_NAME);
                
            } else {
                logger.info("Tabla {} ya existe", TABLE_NAME);
            }
            
        } catch (DynamoDbException e) {
            logger.error("Error al crear tabla DynamoDB: {}", TABLE_NAME, e);
            throw new RuntimeException("Error al crear tabla DynamoDB", e);
        }
    }
}
