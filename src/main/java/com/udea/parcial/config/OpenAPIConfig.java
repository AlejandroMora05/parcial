package com.udea.parcial.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("API de Inventario - Parcial UdeA")
                                                .version("v1")
                                                .description("API RESTful para la gestión de inventarios de productos en diferentes almacenes. "
                                                                +
                                                                "Esta API permite consultar inventarios por almacén y registrar nuevos productos con sus cantidades en stock.")
                                                .contact(new Contact()
                                                                .name("Universidad de Antioquia")
                                                                .email("contacto@udea.edu.co"))
                                                .license(new License()
                                                                .name("Apache 2.0")
                                                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                                .servers(List.of(
                                                new Server()
                                                                .url("http://localhost:8080")
                                                                .description("Servidor de desarrollo local"),
                                                new Server()
                                                                .url("http://localhost:8080")
                                                                .description("Servidor Docker")))
                                .components(new Components()
                                                .addSecuritySchemes("API-Version", new SecurityScheme()
                                                                .type(SecurityScheme.Type.APIKEY)
                                                                .in(SecurityScheme.In.HEADER)
                                                                .name("X-API-Version")
                                                                .description("Header personalizado para versionamiento de API. Valor requerido: 'v1'")));
        }
}
