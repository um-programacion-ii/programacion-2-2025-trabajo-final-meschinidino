# Backend - Sistema de Eventos

Backend implementado según `instrucciones_backend_inicial.md` y alineado a arquitectura limpia.

## Estructura del Proyecto

```
src/main/java/com/eventos/sistemaeventos/
├── domain/                     # Entidades JPA
├── application/                # Casos de uso y puertos
│   ├── dto/catedra/            # DTOs para integración externa
│   ├── port/external/          # Puertos de integración
│   ├── port/repository/        # Puertos de persistencia
│   └── service/                # Lógica de negocio
├── infrastructure/             # Implementaciones técnicas
│   ├── config/                 # Configuración Spring
│   ├── external/               # Cliente del proxy
│   ├── persistence/            # Repositorios JPA
│   └── scheduled/              # Tareas programadas
└── presentation/               # API REST
    ├── controller/
    ├── dto/
    └── exception/
```

## Endpoints Implementados

### Autenticación (`/api/auth`)
- `POST /api/auth/register` - Registrar nuevo usuario
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/logout` - Cerrar sesión
- `GET /api/auth/me` - Obtener usuario actual

### Eventos (`/api/eventos`)
- `GET /api/eventos` - Listar eventos activos
- `GET /api/eventos/{id}` - Obtener evento específico con asientos desde el proxy
- `POST /api/eventos/sincronizar` - Sincronizar eventos desde cátedra
- `POST /api/eventos/{id}/sincronizar` - Sincronizar evento específico

### Sincronización (`/api/sync`)
- `POST /api/sync/webhook` - Recibir cambios de eventos desde el proxy (requiere header `X-Webhook-Token`)

### Sesiones (`/api/sesion`)
- `GET /api/sesion` - Obtener sesión actual
- `POST /api/sesion/paso` - Actualizar paso en sesión
- `POST /api/sesion/seleccionar-asientos` - Seleccionar asientos
- `POST /api/sesion/bloquear-asientos` - Bloquear asientos en cátedra
- `DELETE /api/sesion` - Eliminar sesión

### Ventas (`/api/ventas`)
- `POST /api/ventas/realizar` - Realizar venta
- `GET /api/ventas` - Listar ventas del usuario
- `GET /api/ventas/{id}` - Obtener venta específica
- `POST /api/ventas/sincronizar` - Sincronizar ventas desde cátedra

## Configuración

Variables de entorno requeridas (ver `.env.example` en la raíz del repo):

```properties
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=eventos_db
DB_USER=eventos_user
DB_PASSWORD=eventos_pass

# Server
BACKEND_PORT=8080
PROXY_PORT=8081
SYNC_WEBHOOK_TOKEN=change-me
```

## Compilar y Ejecutar

```bash
# Compilar
./gradlew build

# Ejecutar
./gradlew bootRun

# O ejecutar el JAR
java -jar build/libs/sistema-eventos-0.0.1-SNAPSHOT.jar
```

## Documentación API (Swagger)

El backend incluye **documentación interactiva** con Swagger/OpenAPI:

**Swagger UI**: http://localhost:8080/swagger-ui.html

Desde Swagger UI puedes:
- 📖 Ver todos los endpoints disponibles
- 🧪 Probar endpoints directamente
- 🔐 Autenticarte y hacer requests reales
- 📋 Ver modelos de datos y ejemplos


## Base de Datos

El backend utiliza **PostgreSQL** para persistencia de datos.

Las tablas se crean automáticamente gracias a `spring.jpa.hibernate.ddl-auto=update`.

## Características Implementadas

✅ **Gestión de Usuarios**: Registro, login, autenticación
✅ **Gestión de Eventos**: Sincronización con cátedra, consulta local
✅ **Gestión de Sesiones**: Estado del proceso de compra, timeout automático
✅ **Gestión de Ventas**: Venta de entradas, sincronización con cátedra
✅ **Integración con Proxy**: Consumo de endpoints del proxy para cátedra
✅ **Seguridad**: Spring Security con autenticación básica
✅ **Limpieza Automática**: Sesiones expiradas se limpian cada 10 minutos

## Integración con Cátedra

El cliente `ProxyGatewayClient` consume los endpoints del proxy para:

1. `GET /api/endpoints/v1/eventos-resumidos` - Listado resumido
2. `GET /api/endpoints/v1/eventos` - Listado completo
3. `GET /api/endpoints/v1/evento/{id}` - Evento específico
4. `POST /api/endpoints/v1/bloquear-asientos` - Bloquear asientos
5. `POST /api/endpoints/v1/realizar-venta` - Realizar venta
6. `GET /api/endpoints/v1/listar-ventas` - Listar ventas
7. `GET /api/endpoints/v1/listar-venta/{id}` - Venta específica

## Flujo de Compra

1. Usuario se autentica
2. Lista eventos disponibles
3. Selecciona evento y ve detalle
4. Selecciona asientos
5. Sistema bloquea asientos en cátedra
6. Usuario confirma compra
7. Sistema realiza venta en cátedra
8. Se guarda registro local de la venta

## Notas de Implementación

- Las sesiones expiran después de 30 minutos de inactividad
- Los eventos se sincronizan bajo demanda desde cátedra
- Las ventas se registran localmente y en cátedra
- Spring Security maneja la autenticación con sesiones HTTP
- CORS configurado para desarrollo (localhost:3000, localhost:8080)
