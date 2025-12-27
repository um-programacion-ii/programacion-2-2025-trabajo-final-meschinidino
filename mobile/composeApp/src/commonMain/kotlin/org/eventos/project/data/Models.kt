package org.eventos.project.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.json.JsonElement

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
)

@Serializable
data class LoginResponse(
    val success: Boolean? = null,
    val username: String? = null,
    val message: String? = null,
)

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val firstName: String,
    val lastName: String,
)

@Serializable
data class RegisterResponse(
    val success: Boolean? = null,
    val username: String? = null,
    val message: String? = null,
)

@Serializable
data class EventoTipo(
    val nombre: String? = null,
    val descripcion: String? = null,
)

@Serializable
data class Integrante(
    val nombre: String? = null,
    val apellido: String? = null,
    val identificacion: String? = null,
)

@Serializable
data class Evento(
    val id: Long,
    val titulo: String,
    val resumen: String? = null,
    val descripcion: String? = null,
    val fecha: String? = null,
    val direccion: String? = null,
    val imagen: String? = null,
    val filaAsientos: Int? = null,
    @SerialName("columnaAsientos")
    @JsonNames("columnAsientos")
    val columnaAsientos: Int? = null,
    val precioEntrada: Double? = null,
    val eventoTipo: EventoTipo? = null,
    val integrantes: List<Integrante> = emptyList(),
    val activo: Boolean? = null,
)

@Serializable
data class EventoDetalleResponse(
    val evento: Evento,
    val asientos: JsonElement? = null,
)

@Serializable
data class AsientoSesion(
    val fila: Int,
    val columna: Int,
    val persona: String? = null,
    val bloqueadoEnCatedra: Boolean? = null,
)

@Serializable
data class Sesion(
    val sessionId: String? = null,
    val username: String? = null,
    val eventoId: Long? = null,
    val paso: String? = null,
    val asientosSeleccionados: List<AsientoSesion> = emptyList(),
)

@Serializable
data class SeleccionAsientosRequest(
    val eventoId: Long,
    val asientos: List<AsientoSeleccionado>,
) {
    @Serializable
    data class AsientoSeleccionado(
        val fila: Int,
        val columna: Int,
        val persona: String? = null,
    )
}

@Serializable
data class AsientoEstado(
    val fila: Int,
    val columna: Int,
    val estado: String? = null,
)

@Serializable
data class BloqueoResponse(
    val success: Boolean? = null,
    val descripcion: String? = null,
    val asientos: List<AsientoEstado> = emptyList(),
    val message: String? = null,
)

@Serializable
data class AsientoVenta(
    val fila: Int,
    val columna: Int,
    val persona: String? = null,
    val estado: String? = null,
)

@Serializable
data class Venta(
    val id: Long,
    val eventoId: Long,
    val ventaIdCatedra: Long? = null,
    val fechaVenta: String? = null,
    val precioVenta: Double? = null,
    val resultado: Boolean? = null,
    val descripcion: String? = null,
    val asientos: List<AsientoVenta> = emptyList(),
    val estadoSincronizacion: String? = null,
)

@Serializable
data class VentaResponse(
    val success: Boolean? = null,
    val ventaId: Long? = null,
    val ventaIdCatedra: Long? = null,
    val descripcion: String? = null,
    val precioVenta: Double? = null,
    val asientos: List<AsientoVenta> = emptyList(),
    val message: String? = null,
)

data class Credentials(
    val username: String,
    val password: String,
)

data class SeatKey(
    val fila: Int,
    val columna: Int,
)

data class SeatSelection(
    val seat: SeatKey,
    val persona: String = "",
)
