# 🏦 Sistema Bancario - Gestión de Clientes y Cuentas

Un sistema completo de gestión bancaria que permite administrar clientes, cuentas y transacciones de manera eficiente y moderna.

## 📋 ¿Qué hace este sistema?

Este sistema bancario digital permite:

### 👥 **Gestión de Clientes**
- Registrar nuevos clientes con su información personal completa
- Consultar y actualizar datos de clientes existentes
- Mantener un historial completo de cada cliente

### 💳 **Administración de Cuentas**
- Crear cuentas de ahorro y corrientes
- Consultar saldos y estados de cuenta
- Gestionar múltiples cuentas por cliente
- Monitoreo en tiempo real de los balances

### 💸 **Transacciones Financieras**
- Realizar depósitos y retiros
- Procesar transferencias entre cuentas
- Validaciones automáticas de saldo y límites
- Registro detallado de todas las operaciones

### 📊 **Reportes y Consultas**
- Generar reportes de movimientos por fechas
- Consultar historiales de transacciones
- Exportar reportes en formato JSON y PDF
- Análisis de actividad por cliente y cuenta

## 🏗️ Arquitectura del Sistema

El proyecto está compuesto por dos aplicaciones principales:

### 🔧 **Backend API** (`clients-accounts-transactions-api/`)
- **Tecnología:** Spring Boot con programación reactiva
- **Base de Datos:** PostgreSQL
- **Funcionalidad:** API REST que maneja toda la lógica de negocio
- **Características:** Operaciones asíncronas, validaciones robustas y manejo de errores

### 🎨 **Frontend SPA** (`spa-client-account-viewer/`)
- **Tecnología:** Angular con diseño moderno
- **Funcionalidad:** Interface web intuitiva para usuarios
- **Características:** Dashboard interactivo, formularios dinámicos y reportes visuales

## 🚀 ¿Cómo empezar?

Cada proyecto tiene sus propias instrucciones detalladas de instalación y configuración:

### 📖 **Para levantar el Backend (API)**
Consulta el README en: [`clients-accounts-transactions-api/README.md`](./clients-accounts-transactions-api/README.md)

### 📖 **Para levantar el Frontend (Web)**
Consulta el README en: [`spa-client-account-viewer/README.md`](./spa-client-account-viewer/README.md)

## ✨ Funcionalidades Principales

### 🔍 **Consultas Disponibles**
- Lista de todos los clientes registrados
- Cuentas asociadas a cada cliente
- Movimientos y transacciones por rango de fechas
- Balances actuales e históricos

### 📝 **Operaciones Permitidas**
- **Clientes:** Crear, consultar, actualizar y eliminar
- **Cuentas:** Crear nuevas cuentas, consultar saldos, actualizar información
- **Movimientos:** Registrar depósitos, retiros y transferencias
- **Reportes:** Generar y descargar reportes personalizados

### 🛡️ **Validaciones y Seguridad**
- Validación de saldos antes de operaciones de débito
- Validaciones de integridad de datos
- Manejo robusto de errores y excepciones

## 🗃️ Base de Datos

El sistema utiliza PostgreSQL con las siguientes entidades principales:
- **Personas:** Información básica de individuos
- **Clientes:** Datos bancarios y credenciales
- **Cuentas:** Información de cuentas bancarias
- **Movimientos:** Registro de todas las transacciones

La base de datos incluye datos de ejemplo para pruebas inmediatas.

## 🌟 Características Técnicas Destacadas

- **Arquitectura Reactiva:** Manejo asíncrono y eficiente de operaciones
- **API REST Completa:** Endpoints bien documentados con OpenAPI
- **Interface Moderna:** Diseño responsive y fácil de usar
- **Dockerización:** Fácil despliegue con contenedores
- **Reportes Múltiples:** Exportación en JSON y PDF
- **Validaciones Robustas:** Control de errores en tiempo real

## 📦 Estructura del Proyecto

```
ejercicio-bp/
├── clients-accounts-transactions-api/    # Backend Spring Boot
│   ├── src/                              # Código fuente
│   ├── scripts/                          # Scripts de base de datos
│   ├── docker-compose.yml               # Configuración Docker
│   └── README.md                         # Instrucciones específicas
│
├── spa-client-account-viewer/            # Frontend Angular
│   ├── src/                              # Código fuente
│   ├── public/                           # Archivos estáticos
│   └── README.md                         # Instrucciones específicas
│
└── README.md                             # Este archivo
```

## 🤝 Contribución

Este proyecto sigue las mejores prácticas de desarrollo:
- Código bien documentado y organizado
- Pruebas unitarias completas
- Estándares de codificación consistentes
- Arquitectura escalable y mantenible

---

**¿Listo para empezar?** 🚀 Dirígete a los READMEs específicos de cada proyecto para obtener las instrucciones detalladas de instalación y ejecución.