# 🏦 API Bancaria - Arquitectura Hexagonal

Este proyecto implementa una API bancaria completa usando Spring Boot WebFlux, R2DBC y siguiendo los principios de arquitectura hexagonal para la gestión de clientes, cuentas, movimientos y reportes.

## 🏗️ Estructura del Proyecto

### Arquitectura Hexagonal
El proyecto sigue la arquitectura hexagonal (Ports & Adapters) con la siguiente estructura:

```
src/main/java/com/pichincha/dm/bank/accounts/
├── domain/                     # Núcleo del negocio
│   ├── Account.java           # Entidad de cuenta bancaria
│   ├── Client.java            # Entidad de cliente
│   ├── Movement.java          # Entidad de movimiento
│   ├── Person.java            # Entidad de persona
│   ├── Report.java            # Entidad de reporte
│   ├── enums/                 # Enumeraciones del dominio
│   └── exception/             # Excepciones del dominio
├── application/               # Casos de uso y servicios de aplicación
│   ├── service/              # Servicios de aplicación
│   ├── strategy/             # Estrategias de generación de reportes
│   ├── validation/           # Validaciones de negocio
│   └── command/              # Comandos de operación
└── infrastructure/           # Adaptadores externos
    ├── adapter/
    │   ├── persistence/      # Adaptador de persistencia R2DBC
    │   ├── rest/            # Adaptador web REST
    │   ├── report/          # Adaptadores de generación de reportes
    │   └── pdf/             # Adaptador de generación PDF
    └── config/              # Configuraciones de Spring
```

## 🛠️ Tecnologías Utilizadas

- **Spring Boot 3.2.0** - Framework principal
- **Spring WebFlux** - Programación reactiva y asíncrona
- **Spring Data R2DBC** - Acceso reactivo a base de datos
- **PostgreSQL** - Base de datos relacional
- **OpenAPI Generator** - Generación de código basado en contratos
- **Docker & Docker Compose** - Contenedorización y orquestación
- **Gradle** - Herramienta de construcción y dependencias
- **JUnit 5 & Mockito** - Testing unitario
- **Jackson** - Serialización JSON
- **iText PDF** - Generación de reportes PDF

### Principios SOLID Aplicados
- **SRP**: Cada clase tiene una responsabilidad única
- **OCP**: Extensible mediante interfaces y estrategias
- **LSP**: Las implementaciones son intercambiables
- **ISP**: Interfaces específicas y cohesivas
- **DIP**: Dependencias por abstracción, no por implementaciones concretas

## 🗃️ Configuración de Base de Datos

La aplicación utiliza PostgreSQL con las siguientes configuraciones:
- **Host**: localhost:5432
- **Database**: bank_db
- **Username**: bank_user
- **Password**: bank_password
- **Schema**: bank

### Datos de Ejemplo Incluidos
El sistema incluye datos de prueba automáticamente:
- **2 clientes** con información completa
- **2 cuentas** (una de ahorros y una corriente)
- **4 movimientos** (débitos y créditos de ejemplo)

## 🚀 Cómo ejecutar el proyecto

### Opción 1: Con Docker (Recomendado)

```bash
# 1. Navegar al directorio del proyecto
cd clients-accounts-transactions-api

# 2. Compilar la aplicación
.\gradlew clean build

# 3. Levantar todos los servicios (Base de datos + API + PgAdmin)
docker-compose up --build -d

# 4. Verificar que los contenedores estén corriendo
docker-compose ps
```

### Opción 2: Ejecución Local

```bash
# 1. Levantar solo la base de datos
docker-compose up postgres -d

# 2. Compilar el proyecto
.\gradlew clean build

# 3. Ejecutar la aplicación
.\gradlew bootRun
```

## 🌐 URLs de Acceso

Una vez que la aplicación esté ejecutándose:

- **API REST**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI Docs**: http://localhost:8081/api-docs
- **Base de datos**: localhost:5432
- **PgAdmin**: http://localhost:8080
  - Email: admin@bank.com
  - Password: admin123

## 📋 Endpoints Disponibles

### 👥 **Clientes**
- `GET /api/clientes` - Listar todos los clientes (paginado)
- `GET /api/clientes/{id}` - Obtener cliente por ID
- `POST /api/clientes` - Crear nuevo cliente
- `PUT /api/clientes/{id}` - Actualizar cliente
- `DELETE /api/clientes/{id}` - Eliminar cliente (soft delete)

### 💳 **Cuentas**
- `GET /api/cuentas` - Listar cuentas (filtrable por cliente)
- `GET /api/cuentas/{numeroCuenta}` - Obtener cuenta por número
- `POST /api/cuentas` - Crear nueva cuenta
- `PUT /api/cuentas/{numeroCuenta}` - Actualizar cuenta
- `DELETE /api/cuentas/{numeroCuenta}` - Eliminar cuenta

### 💸 **Movimientos**
- `GET /api/movimientos` - Listar movimientos (filtrable por fecha, cliente, cuenta)
- `GET /api/movimientos/{id}` - Obtener movimiento por ID
- `POST /api/movimientos` - Crear nuevo movimiento (débito/crédito)
- `PUT /api/movimientos/{id}` - Actualizar movimiento
- `DELETE /api/movimientos/{id}` - Eliminar movimiento

### 📊 **Reportes**
- `GET /api/reportes?formato=json` - Generar reporte en JSON
- `GET /api/reportes?formato=pdf` - Generar reporte en PDF
- `GET /api/reportes?formato=pdfBase64` - Generar reporte con PDF en base64

## 🔧 Configuración de Desarrollo

### Variables de Entorno
```properties
# Base de datos
SPRING_R2DBC_URL=r2dbc:postgresql://localhost:5432/bank_db
SPRING_R2DBC_USERNAME=bank_user
SPRING_R2DBC_PASSWORD=bank_password

# Pool de conexiones
SPRING_R2DBC_POOL_ENABLED=true
SPRING_R2DBC_POOL_INITIAL_SIZE=5
SPRING_R2DBC_POOL_MAX_SIZE=20

# Puerto de aplicación
SERVER_PORT=8081
```

## 📊 Monitoreo y Salud

La aplicación incluye Spring Boot Actuator:
- **Health Check**: http://localhost:8081/actuator/health
- **Info**: http://localhost:8081/actuator/info
- **Metrics**: http://localhost:8081/actuator/metrics

## 🧪 Testing

```bash
# Ejecutar todas las pruebas
.\gradlew test

# Ejecutar pruebas con cobertura
.\gradlew test jacocoTestReport

# Ver reporte de cobertura
open build/reports/jacoco/test/html/index.html
```

## 📄 Colección de Postman

El proyecto incluye una colección completa de Postman (`API_Bancaria.postman_collection.json`) con:
- ✅ Todos los endpoints documentados
- ✅ Ejemplos de peticiones y respuestas
- ✅ Variables de entorno configuradas
- ✅ Tests básicos incluidos

### Importar la colección:
1. Abrir Postman
2. File → Import
3. Seleccionar `API_Bancaria.postman_collection.json`
4. Configurar la variable `baseUrl` a `http://localhost:8081`

## 🔍 Características Técnicas

### Programación Reactiva
- Todos los endpoints son **no-bloqueantes**
- Uso de `Mono` y `Flux` para operaciones asíncronas
- Pool de conexiones reactivas con R2DBC

### Validaciones Robustas
- Validación de saldos antes de débitos
- Control de estados de cuentas y clientes
- Manejo centralizado de excepciones
- Validaciones en cadena para movimientos

### Generación de Reportes
- **JSON**: Estructura detallada de movimientos
- **PDF**: Documento formateado profesional
- **PDF Base64**: Para integración con frontends

### Arquitectura Limpia
- **Separación de responsabilidades** clara
- **Inversión de dependencias** aplicada
- **Interfaces bien definidas** entre capas
- **Fácil testing** y mantenimiento

## 🚨 Comandos Útiles

```bash
# Ver logs de la aplicación
docker-compose logs -f bank-app

# Reiniciar solo la aplicación
docker-compose restart bank-app

# Parar todos los servicios
docker-compose down

# Limpiar volúmenes y reimplicar
docker-compose down -v && docker-compose up --build -d

# Acceder a la base de datos directamente
docker-compose exec postgres psql -U bank_user -d bank_db
```

---

**¿Necesitas ayuda?** 🤔 Revisa la documentación de Swagger UI en http://localhost:8081/swagger-ui.html o consulta la colección de Postman incluida.
