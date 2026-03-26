package com.empresa.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RONDA 5a: PRODUCT UPDATED EVENT
 * 
 * Evento publicado cuando se actualiza un producto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdatedEvent {
    private String productId;
    private String name;
    private Double price;
    private String category;
}
