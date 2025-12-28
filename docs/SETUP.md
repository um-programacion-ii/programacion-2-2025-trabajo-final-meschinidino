# Setup del Proyecto

Guía de configuración para ejecutar el stack completo con Docker Compose.

## 1) Variables de Entorno

Crear `.env` en la raíz del repositorio (puede partir de `.env.example`).

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

Notas:
- `SERVICE_JWT_SECRET` debe ser igual en backend y proxy.
- `SYNC_WEBHOOK_TOKEN` es un fallback para el webhook; el mecanismo principal usa JWT.

## 2) Levantar Servicios

```bash
docker compose up -d --build
```

Para detener:

```bash
docker compose down
```

## 3) Verificación Rápida

- Backend: http://localhost:8080/swagger-ui.html
- Proxy health: http://localhost:8081/health
