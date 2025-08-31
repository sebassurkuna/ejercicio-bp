# API Bancaria - Arquitectura Hexagonal

Este proyecto implementa una API bancaria usando Spring Boot WebFlux, R2DBC y siguiendo los principios de arquitectura hexagonal.

## Estructura del Proyecto

### Arquitectura Hexagonal
El proyecto sigue la arquitectura hexagonal (Ports & Adapters) con la siguiente estructura:

```
src/main/java/com/pichincha/dm/bank/accounts/
ÃÄÄ domain/                     # N£cleo del negocio
³   ÃÄÄ model/                  # Entidades del dominio
³   ÃÄÄ port/                   # Puertos (interfaces)
³   ÀÄÄ service/                # Servicios del dominio
ÃÄÄ application/                # Casos de uso
³   ÀÄÄ usecase/               # Implementaci¢n de casos de uso
ÀÄÄ infrastructure/            # Adaptadores externos
    ÃÄÄ adapter/
    ³   ÃÄÄ persistence/       # Adaptador de persistencia
    ³   ÀÄÄ web/              # Adaptador web (REST)
    ÀÄÄ config/               # Configuraciones
```

### Tecnolog¡as Utilizadas
- **Spring Boot 3.2.0** - Framework principal
- **Spring WebFlux** - Programaci¢n reactiva
- **Spring Data R2DBC** - Acceso reactivo a base de datos
- **PostgreSQL** - Base de datos
- **OpenAPI Generator** - Generaci¢n de c¢digo basado en contratos
- **Gradle** - Herramienta de construcci¢n

### Principios SOLID Aplicados
- **SRP**: Cada clase tiene una responsabilidad £nica
- **OCP**: Extensible mediante interfaces
- **LSP**: Las implementaciones son intercambiables
- **ISP**: Interfaces espec¡ficas y cohesivas
- **DIP**: Dependencias por abstracci¢n, no por implementaciones concretas

## Configuraci¢n de Base de Datos

La aplicaci¢n utiliza PostgreSQL con las siguientes configuraciones:
- **Host**: localhost:5432
- **Database**: bank_db
- **Username**: bank_user
- **Password**: bank_password
- **Schema**: bank

## C¢mo ejecutar el proyecto

### 1. Levantar la base de datos
```bash
docker-compose up -d
```

### 2. Compilar el proyecto
```bash
gradlew clean build -x test
```

### 3. Ejecutar la aplicaci¢n
```bash
gradlew bootRun
```

La aplicaci¢n estar  disponible en: http://localhost:8080

## API Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Endpoints Disponibles

### Clientes
- GET /api/clientes - Listar todos los clientes
- GET /api/clientes/{id} - Obtener cliente por ID
- POST /api/clientes - Crear nuevo cliente
- PUT /api/clientes/{id} - Actualizar cliente
- DELETE /api/clientes/{id} - Eliminar cliente

## Monitoreo

La aplicaci¢n incluye Spring Boot Actuator para monitoreo:
- **Health**: http://localhost:8080/actuator/health
- **Info**: http://localhost:8080/actuator/info
- **Metrics**: http://localhost:8080/actuator/metrics
