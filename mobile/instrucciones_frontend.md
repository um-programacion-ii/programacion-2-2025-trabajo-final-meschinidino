Requerimientos Funcionales: Cliente Móvil (Frontend)

Este documento resume las responsabilidades, tecnologías y flujos de usuario que debe implementar la aplicación cliente ("móvil") según el enunciado del Trabajo Final 2025.

1. Tecnología y Definición

Plataforma: Kotlin Multiplatform (KMP).

Rol: Interfaz gráfica única del sistema; interactúa exclusivamente con el Backend del Alumno.

2. Gestión de Sesión y Autenticación

Inicio de Sesión:

El usuario debe ingresar usuario y contraseña para generar una sesión contra el Backend.

Si el usuario inicia sesión en otro dispositivo, debe retomar el proceso exactamente donde lo dejó (ej: si estaba cargando nombres de pasajeros, debe aparecer en esa pantalla).

Cierre/Expiración:

Logout Manual: Debe invalidar los datos locales y, al volver a entrar, reiniciar el proceso desde cero.

Expiración: Si la sesión expira (configurada en backend, ej: 30 min), el próximo inicio de sesión debe arrancar desde el paso 1 (Listado).

3. Flujo Principal de Usuario

El proceso de compra es lineal y debe permitir volver al paso anterior en cualquier momento.

Paso 1: Listado de Eventos

Mostrar una lista de eventos activos obtenidos del Backend.

Datos a mostrar: Información resumida y relevante para entender de qué trata el evento sin ocupar toda la pantalla.

Acción: Permitir seleccionar un evento para ver detalles.

Paso 2: Detalle del Evento

Visualizar información completa del evento.

Acción: Visualizar el mapa de asientos.

Navegación: Botón para "Volver" al listado.

Paso 3: Selección de Asientos (Mapa)

Límite: Permitir seleccionar de 1 a 4 asientos por sesión.

Visualización de Estados (Requerimiento UI):

Los asientos deben diferenciarse visualmente (ej: cambio de color) según su estado:

🟢 Libre

🔴 Vendido (Ocupado)

🔵 Seleccionado por mí (en esta sesión)

⚪ Bloqueado por otro (no disponible temporalmente)

Interacción: Al seleccionar asientos y "Continuar", el sistema debe solicitar el bloqueo al backend.

Navegación: Opción para volver al detalle.

Paso 4: Carga de Datos (Pasajeros)

Una vez bloqueados los asientos, se debe solicitar el Nombre y Apellido para cada uno de los lugares seleccionados.

Navegación: Opción para volver atrás (permite agregar/quitar asientos o reasignar lugares).

Paso 5: Venta (Confirmación)

Pantalla final de confirmación de compra.

Acción: Botón "Comprar" / "Confirmar Venta".

Al realizarse la venta, el cliente debe recibir la confirmación del éxito o fallo (ej: si expiró el tiempo de bloqueo).

Navegación: Opción para volver atrás y modificar nombres.

4. Resumen del Ciclo de Vida (Estados)

Listado (Selección de evento).

Detalle (Ver info).

Selección (Bloqueo de asientos).

Datos (Carga de personas).

Venta (Transacción final).

5. Consideraciones Adicionales

Sincronización: Aunque el backend maneja la lógica pesada, el frontend debe ser capaz de reflejar los cambios de estado (ej: si un asiento se ocupa mientras el usuario lo miraba) al refrescar o intentar avanzar.