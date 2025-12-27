# Proxy de Eventos

Servicio proxy HTTP que consume eventos de Kafka, consulta Redis de cátedra y expone endpoints para el backend.

## Características

- ✅ Endpoints específicos para cátedra y Redis
- ✅ Consumer de Kafka para eventos y notificaciones
- ✅ Health check endpoint
 - ✅ Autenticación servicio a servicio (JWT)

## Endpoints

### Health Check
```
GET /health
```

### Proxy API (para el backend)
Todos los endpoints debajo de `/api/proxy/**` requieren `Authorization: Bearer <jwt>`.

### Endpoint Específico de Redis

**Consultar estado de asientos**
```bash
GET /api/proxy/asientos/{eventoId}

# Ejemplo
curl -H "Authorization: Bearer <jwt>" http://localhost:8081/api/proxy/asientos/1
```

Este endpoint consulta directamente el Redis de cátedra para obtener el estado de los asientos de un evento.
La información se guarda en Redis con la clave `evento:{eventoId}:asientos`.

## Kafka Topics

El proxy consume mensajes de:
- `eventos`: Eventos del sistema
- `notificaciones`: Notificaciones para usuarios

Los eventos se transforman a payload de webhook y se envían al backend por `POST /api/sync/webhook`.

## Variables de Entorno

- `PROXY_PORT`: Puerto del proxy
- `BACKEND_URL`: URL del backend
- `CATEDRA_HOST`: Host base de cátedra (Redis y Kafka)
- `CATEDRA_REDIS_PORT`: Puerto Redis de cátedra
- `CATEDRA_KAFKA_PORT`: Puerto Kafka de cátedra
- `CATEDRA_URL`: URL HTTP de cátedra
- `CATEDRA_TOKEN`: Token JWT de cátedra
- `KAFKA_CONSUMER_GROUP_ID`: ID del grupo de consumidores
- `SYNC_WEBHOOK_TOKEN`: Token compartido para webhook (fallback)
- `SERVICE_JWT_SECRET`: Secreto compartido para JWT entre backend y proxy

## Ejecución

El proxy se levanta con Docker Compose desde la raíz del repositorio:

```bash
docker compose up -d --build
```

## Arquitectura

El proxy actúa como intermediario entre backend y servicios de cátedra, permitiendo:
- Procesamiento asíncrono de eventos vía Kafka
- Consulta del estado de asientos vía Redis
- Notificación de cambios hacia el backend
