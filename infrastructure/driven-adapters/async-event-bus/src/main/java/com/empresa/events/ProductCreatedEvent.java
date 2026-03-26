package com.empresa.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RONDA 5a: PRODUCT CREATED EVENT
 * 
 * Evento publicado cuando se crea un nuevo producto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreatedEvent {
    private String productId;
    private String name;
    private Double price;
    private String category;
}
