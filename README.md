
# Sistema de Gestión de Eventos

Sistema de registro de asistencia a eventos únicos (charlas, cursos, obras de teatro, etc.)

## Arquitectura

- **Backend**: Microservicio Spring Boot/JHipster
- **Proxy**: Microservicio intermediario (Kafka/Redis)
- **Mobile**: Aplicación Kotlin Multiplatform

## Estructura del Proyecto
```
.
├── backend/          # Microservicio Backend
├── proxy/            # Microservicio Proxy
├── mobile/           # Aplicación móvil KMP
└── docs/             # Documentación
```

## Estado del Proyecto

En desarrollo - Trabajo Final 2025

## Configuración Rápida (Docker Compose)

1. Copiar `.env.example` a `.env` y completar valores (incluye `SYNC_WEBHOOK_TOKEN` y `SERVICE_JWT_SECRET`).
2. Levantar servicios:
   ```bash
   docker compose up -d --build
   ```
3. Detener servicios:
   ```bash
   docker compose down
   ```

Para más detalle, ver `docs/SETUP.md`.

## Variables de Entorno Requeridas

```properties
# Servicio de Cátedra
CATEDRA_HOST=192.168.194.250
CATEDRA_URL=http://192.168.194.250:8080
CATEDRA_TOKEN=...

# PostgreSQL (Backend)
DB_HOST=localhost
DB_PORT=5432
DB_NAME=eventos_db
DB_USER=eventos_user
DB_PASSWORD=eventos_pass

# Redis / Kafka Cátedra (Proxy)
CATEDRA_REDIS_PORT=6379
CATEDRA_KAFKA_PORT=9092
KAFKA_CONSUMER_GROUP_ID=tu-consumer-group

# Puertos de servicios
BACKEND_PORT=8080
PROXY_PORT=8081

# Webhook sync + JWT servicio a servicio
SYNC_WEBHOOK_TOKEN=...
SERVICE_JWT_SECRET=...
```
