# API de Gestión de Inventarios - Parcial UdeA

## Descripción
API RESTful para la gestión de inventarios de productos en diferentes almacenes. Desarrollada con Spring Boot y PostgreSQL.

## Características Implementadas

**1. Método GET** - Consultar inventario por almacén  
**2. Método POST** - Registrar productos con stock inicial  
**3. Base de Datos** - PostgreSQL (relacional)  
**4. Documentación** - Swagger/OpenAPI  
**5. Formato JSON** - Todas las respuestas en JSON  
**6. Versionamiento** - Custom Request Header `X-API-Version`  
**7. HATEOAS** - Enlaces hipermedia en respuestas  
**8. Pruebas** - Documentadas con ejemplos  
**9. GitHub** - Repositorio versionado  
**10. Docker** - Contenedores con Docker Compose  

## Requisitos Previos

- Docker y Docker Compose instalados
- Java 17 (solo para desarrollo local)
- Maven 3.8+ (solo para desarrollo local)

## Instalación y Ejecución

### Opción 1: Con Docker (Recomendado)

1. Clonar el repositorio:
```bash
git clone https://github.com/AlejandroMora05/parcial.git
cd parcial