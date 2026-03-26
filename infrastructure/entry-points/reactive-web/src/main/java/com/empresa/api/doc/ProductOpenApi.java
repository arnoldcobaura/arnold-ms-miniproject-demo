package com.empresa.api.doc;

import com.empresa.model.Product;
import lombok.experimental.UtilityClass;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.function.Consumer;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder;
import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;

/**
 * RONDA 4: DOCUMENTACIÓN OPENAPI PARA ROUTERS FUNCIONALES
 * 
 * Define la documentación de cada endpoint siguiendo el patrón de Deposits
 * Usa Consumer<Builder> para retornar configuración de OpenAPI
 */
@UtilityClass
public class ProductOpenApi {

    public static Consumer<Builder> getAllProductsOpenAPI() {
        return ops -> ops
                .tag("Products")
                .operationId("getAllProducts")
                .summary("Obtener todos los productos")
                .description("Retorna una lista de todos los productos disponibles en la base de datos")
                .response(responseBuilder()
                        .responseCode("200")
                        .description("Lista de productos obtenida exitosamente")
                        .implementation(Product.class))
                .build();
    }

    public static Consumer<Builder> getProductByIdOpenAPI() {
        return ops -> ops
                .tag("Products")
                .operationId("getProductById")
                .summary("Obtener producto por ID")
                .description("Retorna un producto específico por su ID")
                .parameter(parameterBuilder()
                        .name("id")
                        .description("ID único del producto")
                        .required(true))
                .response(responseBuilder()
                        .responseCode("200")
                        .description("Producto encontrado")
                        .implementation(Product.class))
                .response(responseBuilder()
                        .responseCode("404")
                        .description("Producto no encontrado"))
                .build();
    }

    public static Consumer<Builder> getExpensiveProductsOpenAPI() {
        return ops -> ops
                .tag("Products")
                .operationId("getExpensiveProducts")
                .summary("Obtener productos caros")
                .description("Retorna productos con precio mayor a $100")
                .response(responseBuilder()
                        .responseCode("200")
                        .description("Lista de productos caros")
                        .implementation(Product.class))
                .build();
    }

    public static Consumer<Builder> createProductOpenAPI() {
        return ops -> ops
                .tag("Products")
                .operationId("createProduct")
                .summary("Crear nuevo producto")
                .description("Crea un nuevo producto en la base de datos con validación")
                .requestBody(requestBodyBuilder()
                        .description("Datos del producto a crear")
                        .required(true)
                        .implementation(Product.class))
                .response(responseBuilder()
                        .responseCode("201")
                        .description("Producto creado exitosamente")
                        .implementation(Product.class))
                .response(responseBuilder()
                        .responseCode("400")
                        .description("Datos inválidos - validación fallida"))
                .build();
    }

    public static Consumer<Builder> updateProductOpenAPI() {
        return ops -> ops
                .tag("Products")
                .operationId("updateProduct")
                .summary("Actualizar producto")
                .description("Actualiza un producto existente con validación")
                .parameter(parameterBuilder()
                        .name("id")
                        .description("ID único del producto")
                        .required(true))
                .requestBody(requestBodyBuilder()
                        .description("Datos del producto a actualizar")
                        .required(true)
                        .implementation(Product.class))
                .response(responseBuilder()
                        .responseCode("200")
                        .description("Producto actualizado exitosamente")
                        .implementation(Product.class))
                .response(responseBuilder()
                        .responseCode("400")
                        .description("Datos inválidos - validación fallida"))
                .response(responseBuilder()
                        .responseCode("404")
                        .description("Producto no encontrado"))
                .build();
    }

    public static Consumer<Builder> deleteProductOpenAPI() {
        return ops -> ops
                .tag("Products")
                .operationId("deleteProduct")
                .summary("Eliminar producto")
                .description("Elimina un producto de la base de datos")
                .parameter(parameterBuilder()
                        .name("id")
                        .description("ID único del producto")
                        .required(true))
                .response(responseBuilder()
                        .responseCode("204")
                        .description("Producto eliminado exitosamente"))
                .response(responseBuilder()
                        .responseCode("404")
                        .description("Producto no encontrado"))
                .build();
    }
}
