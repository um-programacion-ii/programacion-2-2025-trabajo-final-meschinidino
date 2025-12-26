
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

## Configuración Rápida

1. Copiar `.env.example` a `.env` y completar valores.
2. Ejecutar con Docker Compose:
   ```bash
   ./scripts/dev-up-docker.sh
   ```
3. Detener servicios:
   ```bash
   ./scripts/dev-down-docker.sh
   ```

## Ejecución Local (sin Docker)

```bash
./scripts/dev-up-local.sh
```

Para detener:
```bash
./scripts/dev-down-local.sh
```
