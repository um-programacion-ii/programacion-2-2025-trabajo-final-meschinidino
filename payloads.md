Documentación de Payloads: API Cátedra (Consumo Backend)

Este documento detalla los endpoints y estructuras JSON estrictamente necesarios para que el Backend del alumno interactúe con el servicio de la Cátedra.

Nota: Todas las peticiones requieren el Header de autorización:
Authorization: Bearer <tu_token_jwt>

1. Autenticación

1.1 Login de Usuario (Backend)

Endpoint utilizado por el Backend para autenticarse y renovar su token de acceso si este ha expirado.

URL: /api/authenticate

Método: POST

Request (JSON Entrada):

{
"username": "tu_usuario_catedra",
"password": "tu_password",
"rememberMe": false
}


Response (JSON Salida):

{
"id_token": "eyJhbGciOiJITzUxMiJ9.eyJzdWIiOiJ..."
}


2. Sincronización de Eventos (Lectura)

El Backend consume estos servicios para poblar y mantener actualizada su Base de Datos Local.

2.1 Listado Completo de Eventos

Devuelve todos los datos (incluyendo imágenes, dirección e integrantes). Es el endpoint principal para la sincronización inicial o periódica.

URL: /api/endpoints/v1/eventos

Método: GET

Response (JSON Salida - Array):

[
{
"id": 1,
"titulo": "Conferencia Nerd",
"resumen": "Esta es una conferencia de Nerds",
"descripcion": "Descripción completa...",
"fecha": "2025-11-10T11:00:00Z",
"direccion": "Aula magna de la Universidad de Mendoza",
"imagen": "[https://url-imagen.com/img.jpg](https://url-imagen.com/img.jpg)",
"filaAsientos": 10,
"columnAsientos": 20,
"precioEntrada": 2500.00,
"eventoTipo": {
"nombre": "Conferencia",
"descripcion": "Conferencia"
},
"integrantes": [
{
"nombre": "María",
"apellido": "Corvalán",
"identificacion": "Dra."
}
]
}
]


2.2 Detalle de un Evento

Devuelve los datos completos de un solo evento específico. Útil para actualizar un evento puntual tras recibir una notificación de cambio (vía Proxy/Kafka).

URL: /api/endpoints/v1/evento/{id}

Método: GET

Response (JSON Salida):
Misma estructura que un objeto individual del Endpoint 2.1.

3. Transacciones (Operaciones)

Endpoints críticos que el Backend llama cuando un usuario realiza acciones en la App Móvil.

3.1 Bloquear Asientos

Se llama cuando un usuario selecciona asientos en la App. El Backend debe orquestar este bloqueo contra la Cátedra.

URL: /api/endpoints/v1/bloquear-asientos

Método: POST

Request (JSON Entrada):

{
"eventoId": 1,
"asientos": [
{ "fila": 2, "columna": 1 },
{ "fila": 2, "columna": 2 }
]
}


Response (JSON Salida - Éxito):

{
"resultado": true,
"descripcion": "Asientos bloqueados con exito",
"eventoId": 1,
"asientos": [
{ "estado": "Bloqueo exitoso", "fila": 2, "columna": 1 },
{ "estado": "Bloqueo exitoso", "fila": 2, "columna": 2 }
]
}


Response (JSON Salida - Fallo):

{
"resultado": false,
"descripcion": "No todos los asientos pueden ser bloqueados",
"eventoId": 1,
"asientos": [
{ "estado": "Ocupado", "fila": 2, "columna": 1 },
{ "estado": "Ocupado", "fila": 2, "columna": 2 }
]
}


3.2 Realizar Venta (Confirmar Compra)

Se llama cuando el usuario confirma el pago. El Backend envía los datos finales a la Cátedra para registrar la venta oficial.

URL: /api/endpoints/v1/realizar-venta

Método: POST

Request (JSON Entrada):

{
"eventoId": 1,
"fecha": "2025-08-17T20:00:00.000Z",
"precioVenta": 1400.10,
"asientos": [
{
"fila": 2,
"columna": 3,
"persona": "Fernando Galvez"
},
{
"fila": 2,
"columna": 4,
"persona": "Carlos Perez"
}
]
}


Response (JSON Salida - Éxito):

{
"eventoId": 1,
"ventaId": 1506,
"fechaVenta": "2025-08-24T23:18:41.974720Z",
"asientos": [
{
"fila": 2,
"columna": 3,
"persona": "Fernando Galvez",
"estado": "Vendido"
},
{
"fila": 2,
"columna": 4,
"persona": "Carlos Perez",
"estado": "Vendido"
}
],
"resultado": true,
"descripcion": "Venta realizada con exito",
"precioVenta": 1400.0
}


Response (JSON Salida - Fallo):

{
"eventoId": 1,
"resultado": false,
"descripcion": "Venta rechazada. Alguno de los asientos no se encontraban bloqueados para la venta.",
"precioVenta": 1400.0,
"asientos": [
{ "fila": 2, "columna": 3, "persona": "Fernando Galvez", "estado": "Libre" }
]
}
