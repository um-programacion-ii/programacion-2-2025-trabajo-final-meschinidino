# 📚 Documentación API con Swagger/OpenAPI

## ✅ Swagger Configurado

El backend ahora cuenta con **documentación automática** usando SpringDoc OpenAPI (Swagger).

## 🌐 Acceder a la Documentación

Una vez que el backend esté corriendo, puedes acceder a:

### Swagger UI (Interfaz Interactiva)
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI Specification (JSON)
```
http://localhost:8080/v3/api-docs
```

## 🎯 Características

### ✅ Interfaz Interactiva
- **Try it out**: Probar endpoints directamente desde el navegador
- **Autenticación**: Botón "Authorize" para configurar Basic Auth
- **Validación**: Ver estructuras de request/response
- **Ejemplos**: Código de ejemplo para cada endpoint

### ✅ Organización por Tags
Los endpoints están organizados en categorías:
- 🔐 **Autenticación**: Login, registro, logout
- 📅 **Eventos**: Listado, detalle, sincronización
- 🎫 **Sesiones**: Selección y bloqueo de asientos
- 💰 **Ventas**: Realizar compra, historial

### ✅ Autenticación Incluida
- Swagger está configurado con **HTTP Basic Auth**
- Click en "Authorize" → Ingresar username y password
- Todos los endpoints protegidos se probarán con esas credenciales

## 🚀 Cómo Usar

### 1. Iniciar el Backend
```bash
cd backend
./gradlew bootRun
```

### 2. Abrir Swagger UI
Navegar a: http://localhost:8080/swagger-ui.html

### 3. Probar un Endpoint Público
1. Expandir la sección **Autenticación**
2. Click en `POST /api/auth/register`
3. Click en "Try it out"
4. Modificar el JSON de ejemplo:
```json
{
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "firstName": "Test",
  "lastName": "User"
}
```
5. Click en "Execute"
6. Ver la respuesta

### 4. Configurar Autenticación
1. Click en el botón **"Authorize"** (arriba a la derecha)
2. Ingresar:
   - **Username**: `testuser`
   - **Password**: `password123`
3. Click en "Authorize"
4. Click en "Close"

### 5. Probar Endpoints Protegidos
Ahora puedes probar cualquier endpoint que requiera autenticación:
1. Expandir **Eventos**
2. Click en `GET /api/eventos`
3. Click en "Try it out"
4. Click en "Execute"
5. Ver la lista de eventos

## 📖 Comparación con Swag (Go)

Similar a Swag en Go con Gin, SpringDoc usa **anotaciones** en el código:

### En Go con Swag:
```go
// @Summary      List events
// @Description  Get all active events
// @Tags         eventos
// @Accept       json
// @Produce      json
// @Success      200  {array}  Event
// @Router       /eventos [get]
func ListEventos(c *gin.Context) {
    // ...
}
```

### En Java con SpringDoc:
```java
@Operation(
    summary = "Listar eventos activos",
    description = "Retorna todos los eventos activos disponibles"
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Lista de eventos")
})
@GetMapping("/eventos")
public ResponseEntity<List<Evento>> listarEventos() {
    // ...
}
```

## 🎨 Anotaciones Disponibles

### A Nivel de Clase (Controller)
```java
@Tag(name = "Eventos", description = "Gestión de eventos")
@SecurityRequirement(name = "basicAuth")
```

### A Nivel de Método
```java
@Operation(summary = "...", description = "...")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "OK"),
    @ApiResponse(responseCode = "404", description = "Not Found")
})
```

### En Parámetros
```java
@Parameter(description = "ID del evento")
@PathVariable Long id
```

### En Request Bodies
```java
@io.swagger.v3.oas.annotations.parameters.RequestBody(
    description = "Datos del usuario",
    required = true
)
@RequestBody UserDTO user
```

## 🔧 Configuración

La configuración está en:
- **Clase**: `OpenAPIConfig.java`
- **YAML**: `application.yaml` (sección `springdoc`)

### Personalizar Información
Editar `OpenAPIConfig.java`:
```java
.info(new Info()
    .title("Tu Título")
    .version("2.0.0")
    .description("Tu descripción")
    .contact(new Contact()
        .name("Tu Nombre")
        .email("tu@email.com")))
```

### Agregar Servidor
```java
.servers(List.of(
    new Server()
        .url("https://mi-api.com")
        .description("Producción")))
```

## 📥 Exportar Documentación

### Descargar OpenAPI Spec
```bash
curl http://localhost:8080/v3/api-docs > openapi.json
```

### Convertir a YAML
```bash
curl http://localhost:8080/v3/api-docs.yaml > openapi.yaml
```

### Importar en Postman
1. En Postman: Import → Link
2. Pegar: `http://localhost:8080/v3/api-docs`
3. Se importarán todos los endpoints automáticamente

### Generar Cliente
Usar OpenAPI Generator para generar clientes en cualquier lenguaje:
```bash
openapi-generator-cli generate \
  -i http://localhost:8080/v3/api-docs \
  -g typescript-axios \
  -o ./client
```

## 🎯 Endpoints Documentados

### Públicos (sin autenticación)
- ✅ `POST /api/auth/register` - Registro
- ✅ `POST /api/auth/login` - Login

### Protegidos (requieren autenticación)
- ✅ `GET /api/auth/me` - Usuario actual
- ✅ `POST /api/auth/logout` - Logout
- ✅ `GET /api/eventos` - Listar eventos
- ✅ `GET /api/eventos/{id}` - Detalle de evento
- ✅ `POST /api/eventos/sincronizar` - Sincronizar eventos
- ✅ `POST /api/eventos/{id}/sincronizar` - Sincronizar evento
- ✅ `GET /api/sesion` - Obtener sesión
- ✅ `POST /api/sesion/paso` - Actualizar paso
- ✅ `POST /api/sesion/seleccionar-asientos` - Seleccionar asientos
- ✅ `POST /api/sesion/bloquear-asientos` - Bloquear asientos
- ✅ `DELETE /api/sesion` - Eliminar sesión
- ✅ `POST /api/ventas/realizar` - Realizar venta
- ✅ `GET /api/ventas` - Mis ventas
- ✅ `GET /api/ventas/{id}` - Detalle de venta
- ✅ `POST /api/ventas/sincronizar` - Sincronizar ventas

## 🌟 Ventajas

### vs Postman Collection
- ✅ **Siempre actualizada**: Se genera del código
- ✅ **Interactiva**: Probar sin salir del navegador
- ✅ **Versionada**: Va con el código en Git
- ✅ **Estándar**: OpenAPI es un estándar de la industria

### vs Documentación Manual
- ✅ **Automática**: No hay que mantenerla manualmente
- ✅ **Precisa**: Refleja exactamente el código
- ✅ **Validación**: Se puede validar con herramientas
- ✅ **Generación de código**: Clientes y SDKs automáticos

## 🔗 Links Útiles

- **Swagger UI Local**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml
- **SpringDoc Docs**: https://springdoc.org/
- **OpenAPI Spec**: https://swagger.io/specification/

## 🛠️ Troubleshooting

### No aparece Swagger UI
- Verificar que el backend esté corriendo: `curl http://localhost:8080/v3/api-docs`
- Revisar `application.yaml`: sección `springdoc`
- Verificar SecurityConfig: endpoints `/swagger-ui/**` permitidos

### Endpoints no aparecen
- Verificar que los controllers tengan `@RestController`
- Verificar que los métodos tengan `@GetMapping`, `@PostMapping`, etc.
- Reiniciar el backend

### Autenticación no funciona
- Click en "Authorize"
- Ingresar username y password correctos
- Si no funciona, registrar usuario primero con `/api/auth/register`

## 💡 Tips

1. **Usar "Try it out"**: Probar todos los endpoints directamente
2. **Revisar Schemas**: Ver modelos de datos al final de la página
3. **Copiar curl**: Cada request tiene su comando curl equivalente
4. **Exportar**: Descargar el OpenAPI spec para compartir con el equipo
5. **Filtrar**: Usar la barra de búsqueda para encontrar endpoints rápido

---

**¡La documentación ahora es parte del código! 🎉**

Cada vez que agregues o modifiques un endpoint, la documentación se actualiza automáticamente.

