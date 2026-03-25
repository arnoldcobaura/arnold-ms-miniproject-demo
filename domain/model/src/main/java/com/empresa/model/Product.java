package com.empresa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PASO 4: ENTIDAD DEL DOMINIO - CLASE PURA DEL NEGOCIO
 * 
 * Características:
 * - Sin anotaciones de frameworks (Spring, JPA, etc.)
 * - Solo lógica de negocio pura
 * - Lombok para reducir boilerplate
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    private String id;
    private String name;
    private Double price;
    private String category;
    
    /**
     * Lógica de negocio: producto es caro si cuesta más de $100
     */
    public boolean isExpensive() {
        return this.price != null && this.price > 100.0;
    }
    
    /**
     * Validación de negocio: datos válidos
     */
    public boolean isValid() {
        return name != null && !name.trim().isEmpty()
            && price != null && price > 0
            && category != null && !category.trim().isEmpty();
    }
}
