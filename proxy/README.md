# Proxy de Eventos

Servicio proxy HTTP que hace forward de requests al backend y consume eventos de Kafka.

## Características

- ✅ Forward HTTP (GET, POST, PUT, DELETE) al backend
- ✅ Consumer de Kafka para eventos y notificaciones
- ✅ Health check endpoint

## Endpoints

### Health Check
```
GET /health
```

### Proxy API
Todos los requests a `/api/**` se forwardean automáticamente al backend.

Ejemplos:
```
GET  /api/eventos     -> GET  http://backend:8080/api/eventos
POST /api/eventos     -> POST http://backend:8080/api/eventos
PUT  /api/eventos/123 -> PUT  http://backend:8080/api/eventos/123
DELETE /api/eventos/123 -> DELETE http://backend:8080/api/eventos/123
```

### Endpoint Específico de Redis

**Consultar estado de asientos**
```bash
GET /api/asientos/{eventoId}

# Ejemplo
curl http://localhost:8081/api/asientos/1
```

Este endpoint consulta directamente el Redis de cátedra para obtener el estado de los asientos de un evento.
La información se guarda en Redis con la clave `evento:{eventoId}:asientos`.

## Kafka Topics

El proxy consume mensajes de:
- `eventos`: Eventos del sistema
- `notificaciones`: Notificaciones para usuarios

## Variables de Entorno

- `PROXY_PORT`: Puerto del proxy (default: 8081)
- `BACKEND_URL`: URL del backend (default: http://localhost:8080)
- `CATEDRA_HOST`: Host base de cátedra (Redis y Kafka)
- `CATEDRA_REDIS_PORT`: Puerto Redis de cátedra (default: 6379)
- `CATEDRA_KAFKA_PORT`: Puerto Kafka de cátedra (default: 9092)
- `CATEDRA_URL`: URL HTTP de cátedra
- `CATEDRA_TOKEN`: Token JWT de cátedra
- `KAFKA_CONSUMER_GROUP_ID`: ID del grupo de consumidores

## Ejecución

**Opción 1: Usando el script (recomendado)**
```bash
./run-proxy.sh
```

**Opción 2: Exportando variables manualmente**
```bash
# Desde la raíz del proyecto
source <(cat .env | grep -v '^#' | grep -v '^$' | sed 's/^/export /')
cd proxy
./gradlew bootRun
```

**Nota:** El proxy requiere que las variables de entorno estén configuradas en el archivo `.env` en la raíz del proyecto.
Variables clave: `CATEDRA_HOST`, `CATEDRA_URL`, `CATEDRA_TOKEN`, `CATEDRA_REDIS_PORT`, `CATEDRA_KAFKA_PORT`, `KAFKA_CONSUMER_GROUP_ID`.

## Arquitectura

El proxy actúa como intermediario entre clientes y el backend, permitiendo:
- Balanceo de carga
- Logging centralizado
- Procesamiento asíncrono de eventos vía Kafka
- Integración con Redis de cátedra para sesiones compartidas
