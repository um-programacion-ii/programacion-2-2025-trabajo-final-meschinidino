package com.eventos.sistemaeventos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger
 * 
 * Documentación disponible en:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenAPIConfig {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Bean
    public OpenAPI customOpenAPI() {
        // Configurar Basic Auth
        final String securitySchemeName = "basicAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Eventos - API")
                        .version("1.0.0")
                        .description("""
                                API REST para el sistema de gestión de eventos.
                                
                                ## Autenticación
                                La mayoría de los endpoints requieren autenticación HTTP Basic.
                                
                                ## Flujo de Compra
                                1. Registrar usuario: `POST /api/auth/register`
                                2. Login: `POST /api/auth/login`
                                3. Listar eventos: `GET /api/eventos`
                                4. Ver detalle: `GET /api/eventos/{id}`
                                5. Seleccionar asientos: `POST /api/sesion/seleccionar-asientos`
                                6. Bloquear asientos: `POST /api/sesion/bloquear-asientos`
                                7. Realizar venta: `POST /api/ventas/realizar`
                                
                                ## Integración con Cátedra
                                El sistema se sincroniza con la API de cátedra para obtener eventos
                                y registrar ventas. Los endpoints de sincronización están documentados
                                en la sección de Eventos.
                                """)
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("dev@eventos.com")
                                .url("https://github.com/tu-repo/eventos"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor de Desarrollo"),
                        new Server()
                                .url("https://api.eventos.com")
                                .description("Servidor de Producción (si aplica)")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("Autenticación HTTP Basic. Usar username y password del usuario registrado.")));
    }
}

