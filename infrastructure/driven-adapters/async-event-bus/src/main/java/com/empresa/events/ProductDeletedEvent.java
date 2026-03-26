package com.empresa.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RONDA 5a: PRODUCT DELETED EVENT
 * 
 * Evento publicado cuando se elimina un producto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDeletedEvent {
    private String productId;
}
