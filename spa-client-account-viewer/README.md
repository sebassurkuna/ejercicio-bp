# 🌐 SPA Cliente - Visor de Cuentas Bancarias

Aplicación web Angular moderna para la gestión y visualización de clientes, cuentas bancarias, movimientos y reportes del sistema bancario.

## 📋 Descripción del Proyecto

Esta aplicación SPA (Single Page Application) desarrollada en Angular 18 proporciona una interfaz de usuario intuitiva para:

### 👥 **Gestión de Clientes**
- Visualización de lista de clientes con paginación
- Formularios para crear y actualizar clientes
- Información detallada de cada cliente y sus datos personales

### 💳 **Administración de Cuentas**
- Vista de cuentas por cliente
- Información de saldos y estados de cuenta
- Creación de nuevas cuentas bancarias

### 💸 **Movimientos y Transacciones**
- Historial de movimientos con filtros por fecha
- Visualización de débitos y créditos
- Consulta de saldos posteriores a transacciones

### 📊 **Reportes Interactivos**
- Generación de reportes en tiempo real
- Filtros por cliente y rango de fechas
- Descarga de reportes en formato PDF
- Vista previa de reportes en JSON

## 🏗️ Arquitectura del Proyecto

```
src/
├── app/
│   ├── components/          # Componentes reutilizables
│   │   ├── data-table/     # Tabla de datos genérica
│   │   ├── header/         # Cabecera de la aplicación
│   │   ├── page-header/    # Encabezado de páginas
│   │   ├── router/         # Manejo de navegación
│   │   └── sidebar/        # Menú lateral
│   ├── layouts/
│   │   └── dashboard/      # Layout principal del dashboard
│   ├── pages/              # Páginas principales
│   │   ├── accounts/       # Gestión de cuentas
│   │   ├── client-form/    # Formulario de clientes
│   │   ├── clients/        # Lista de clientes
│   │   ├── movements/      # Gestión de movimientos
│   │   └── reports/        # Generación de reportes
│   ├── services/           # Servicios de API
│   │   └── client/         # Servicio de clientes
│   ├── interfaces/         # Interfaces TypeScript
│   └── const/              # Constantes de la aplicación
└── environments/           # Configuraciones de entorno
```

## 🛠️ Tecnologías Utilizadas

- **Angular 18.2.20** - Framework principal
- **TypeScript** - Lenguaje de programación
- **SCSS** - Preprocesador CSS
- **Angular CLI** - Herramientas de desarrollo
- **RxJS** - Programación reactiva
- **Jest** - Testing unitario
- **Angular Router** - Navegación SPA

## 🚀 Instalación y Configuración

### Prerrequisitos
- Node.js (versión 18 o superior)
- npm o yarn
- Angular CLI (`npm install -g @angular/cli`)

### Configuración Inicial

```bash
# 1. Navegar al directorio del proyecto
cd spa-client-account-viewer

# 2. Instalar dependencias
npm install

# 3. Verificar que el backend esté corriendo en http://localhost:8081
# (Consultar README del proyecto clients-accounts-transactions-api)
```

## 🖥️ Ejecución del Proyecto

### Servidor de Desarrollo con Proxy

**⚠️ IMPORTANTE:** Para el correcto funcionamiento, ejecutar con configuración de proxy:

```bash
# Iniciar servidor de desarrollo con proxy
ng serve --proxy-config proxy.conf.json

# O alternativamente
npm start -- --proxy-config proxy.conf.json
```

### 🌐 **Configuración de CORS**

Para evitar problemas de CORS al consumir la API, **debes habilitar CORS en tu navegador**:

#### **Google Chrome:**
```bash
# Windows
chrome.exe --user-data-dir="C:/Chrome dev" --disable-web-security --disable-features=VizDisplayCompositor

# macOS
open -n -a /Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --args --user-data-dir="/tmp/chrome_dev_test" --disable-web-security

# Linux
google-chrome --disable-web-security --disable-features=VizDisplayCompositor --user-data-dir="/tmp/chrome_dev_test"
```

#### **Firefox:**
1. Ir a `about:config`
2. Buscar `security.tls.insecure_fallback_hosts`
3. Agregar `localhost`

### URLs de Acceso

Una vez ejecutado correctamente:
- **Aplicación Web**: http://localhost:4200
- **API Backend**: http://localhost:8081 (debe estar corriendo)

## ⚙️ Configuración del Proxy

El archivo `proxy.conf.json` redirige las peticiones `/api` al backend:

```json
{
  "/api": {
    "target": "http://localhost:8081",
    "secure": false,
    "changeOrigin": true
  }
}
```

Esto permite que las llamadas desde `http://localhost:4200/api/*` se redirijan a `http://localhost:8081/api/*`.

## 🧪 Testing

### Pruebas Unitarias
```bash
# Ejecutar pruebas unitarias con Jest
npm test

# Ejecutar pruebas con coverage
npm run test:coverage

# Ejecutar pruebas en modo watch
npm run test:watch
```

### Estructura de Testing
- Cada componente tiene su archivo `.spec.ts`
- Servicios tienen pruebas unitarias completas
- Configuración Jest en `jest.config.js`

## 🔨 Build y Despliegue

### Build de Desarrollo
```bash
ng build
```

### Build de Producción
```bash
ng build --configuration production
```

Los archivos se generan en la carpeta `dist/`.

### Variables de Entorno

#### `environment.local.ts`
```typescript
export const environment = {
  production: false,
  apiUrl: '/api',
  apiBaseUrl: 'http://localhost:8081'
};
```

#### `environment.prod.ts`
```typescript
export const environment = {
  production: true,
  apiUrl: '/api',
  apiBaseUrl: 'https://your-api-domain.com'
};
```

## 📱 Funcionalidades Principales

### 🎯 **Dashboard Principal**
- Navegación lateral con menús organizados
- Cabecera con información del sistema
- Layout responsive para diferentes dispositivos

### 📊 **Tabla de Datos Genérica**
- Componente reutilizable para mostrar información
- Paginación automática
- Ordenamiento por columnas
- Filtros integrados

### 🔄 **Gestión de Estado**
- Servicios reactivos con RxJS
- Manejo de estado local por componente
- Comunicación eficiente con la API

### 🎨 **Diseño Responsive**
- Compatible con dispositivos móviles
- Diseño moderno y limpio
- Consistencia visual en toda la aplicación

## 🚨 Solución de Problemas

### Error de CORS
```
Access to XMLHttpRequest at 'http://localhost:8081/api/...' from origin 'http://localhost:4200' has been blocked by CORS policy
```
**Solución:** Ejecutar el navegador con CORS deshabilitado (ver sección de configuración CORS).

### Error de Proxy
```
[HPM] Error occurred while trying to proxy request
```
**Solución:** 
1. Verificar que el backend esté corriendo en puerto 8081
2. Asegurar que el comando incluya `--proxy-config proxy.conf.json`

### Dependencias
```bash
# Limpiar cache de npm
npm cache clean --force

# Reinstalar dependencias
rm -rf node_modules package-lock.json
npm install
```

## 📦 Scripts Disponibles

```bash
# Desarrollo con proxy
npm start -- --proxy-config proxy.conf.json

# Build de producción  
npm run build

# Tests unitarios
npm test

# Linting
npm run lint

# Formatear código
npm run format
```

## 🔗 Integración con Backend

La aplicación consume los siguientes endpoints de la API:

- **Clientes**: `/api/clientes`
- **Cuentas**: `/api/cuentas` 
- **Movimientos**: `/api/movimientos`
- **Reportes**: `/api/reportes`

Consultar el README del backend para más detalles sobre la API.

## 🤝 Desarrollo

### Generar Nuevos Componentes
```bash
ng generate component pages/nueva-pagina
ng generate service services/nuevo-servicio
ng generate interface interfaces/nueva-interface
```

### Estructura de Archivos
- **Componentes**: Un componente por funcionalidad
- **Servicios**: Lógica de negocio y comunicación con API
- **Interfaces**: Tipado TypeScript para datos
- **Constantes**: Configuraciones y datos estáticos

---

**¿Necesitas ayuda?** 🤔 Asegúrate de que el backend esté corriendo en puerto 8081 y que hayas habilitado CORS en tu navegador antes de iniciar el desarrollo.
