# Backend - Sistema de Eventos

Backend implementado según `instrucciones_backend_inicial.md`.

## Estructura del Proyecto

```
src/main/java/com/eventos/sistemaeventos/
├── domain/              # Entidades JPA
│   ├── Usuario.java
│   ├── Evento.java
│   ├── Sesion.java
│   ├── Venta.java
│   ├── AsientoSesion.java
│   ├── AsientoVenta.java
│   ├── Integrante.java
│   └── EventoTipo.java
├── repository/          # Repositorios JPA
│   ├── UsuarioRepository.java
│   ├── EventoRepository.java
│   ├── SesionRepository.java
│   └── VentaRepository.java
├── service/            # Lógica de negocio
│   ├── CatedraService.java      # Integración con cátedra
│   ├── EventoService.java       # Gestión de eventos
│   ├── SesionService.java       # Gestión de sesiones
│   ├── VentaService.java        # Gestión de ventas
│   └── UsuarioService.java      # Gestión de usuarios
├── controller/         # API REST
│   ├── AuthController.java
│   ├── EventoController.java
│   ├── SesionController.java
│   └── VentaController.java
├── dto/               # DTOs
│   ├── catedra/       # DTOs para API de cátedra
│   └── ...            # DTOs para API propia
├── config/            # Configuración
│   ├── SecurityConfig.java
│   └── RestTemplateConfig.java
└── scheduled/         # Tareas programadas
    └── ScheduledTasks.java
```

## Endpoints Implementados

### Autenticación (`/api/auth`)
- `POST /api/auth/register` - Registrar nuevo usuario
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/logout` - Cerrar sesión
- `GET /api/auth/me` - Obtener usuario actual

### Eventos (`/api/eventos`)
- `GET /api/eventos` - Listar eventos activos
- `GET /api/eventos/{id}` - Obtener evento específico
- `POST /api/eventos/sincronizar` - Sincronizar eventos desde cátedra
- `POST /api/eventos/{id}/sincronizar` - Sincronizar evento específico

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

Variables de entorno requeridas (ver `.env.example`):

```properties
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=eventos_db
DB_USER=eventos_user
DB_PASSWORD=eventos_pass

# Redis
REDIS_LOCAL_HOST=localhost
REDIS_LOCAL_PORT=6379

# Cátedra
CATEDRA_URL=https://catedra-api.com
CATEDRA_TOKEN=tu-token-aqui

# Server
BACKEND_PORT=8080
PROXY_PORT=8081
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

Ver guía completa en: **[SWAGGER.md](SWAGGER.md)**

## Base de Datos

El backend utiliza:
- **PostgreSQL** para persistencia de datos
- **Redis** para gestión de sesiones HTTP

Las tablas se crean automáticamente gracias a `spring.jpa.hibernate.ddl-auto=update`.

## Características Implementadas

✅ **Gestión de Usuarios**: Registro, login, autenticación
✅ **Gestión de Eventos**: Sincronización con cátedra, consulta local
✅ **Gestión de Sesiones**: Estado del proceso de compra, timeout automático
✅ **Gestión de Ventas**: Venta de entradas, sincronización con cátedra
✅ **Integración con Cátedra**: Consumo de todos los endpoints requeridos
✅ **Seguridad**: Spring Security con autenticación básica
✅ **Limpieza Automática**: Sesiones expiradas se limpian cada 10 minutos

## Integración con Cátedra

El servicio `CatedraService` implementa todos los endpoints requeridos:

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
- Spring Security maneja la autenticación con sesiones HTTP en Redis
- CORS configurado para desarrollo (localhost:3000, localhost:8080)

