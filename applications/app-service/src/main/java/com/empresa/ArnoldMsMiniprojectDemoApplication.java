package com.empresa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PASO 10: APLICACIÓN PRINCIPAL - PUNTO DE ENTRADA
 * 
 * @SpringBootApplication escanea todos los componentes en com.empresa y subpaquetes
 * Esto incluye:
 * - @Component en ProductRepositoryAdapter
 * - @Configuration en RouterRest
 * - Todos los beans necesarios
 */
@SpringBootApplication
public class ArnoldMsMiniprojectDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArnoldMsMiniprojectDemoApplication.class, args);
	}

}
