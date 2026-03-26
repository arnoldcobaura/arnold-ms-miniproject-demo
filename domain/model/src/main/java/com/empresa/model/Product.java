package com.empresa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * PASO 4: ENTIDAD DEL DOMINIO - CLASE PURA DEL NEGOCIO
 * 
 * RONDA 3: Validaciones con Bean Validation
 * 
 * Características:
 * - Sin anotaciones de frameworks (Spring, JPA, etc.)
 * - Solo lógica de negocio pura
 * - Lombok para reducir boilerplate
 * - Validaciones declarativas con Jakarta Validation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    private String id;
    
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;
    
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double price;
    
    @NotBlank(message = "La categoría es obligatoria")
    @Size(min = 2, max = 50, message = "La categoría debe tener entre 2 y 50 caracteres")
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
