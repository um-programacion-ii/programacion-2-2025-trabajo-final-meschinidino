package org.eventos.project.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.ContentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.encodeBase64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class ApiClient(private val baseUrl: String) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
    }

    private fun HttpRequestBuilder.applyAuth(credentials: Credentials?) {
        if (credentials != null) {
            val token = "${credentials.username}:${credentials.password}"
                .encodeToByteArray()
                .encodeBase64()
            header(HttpHeaders.Authorization, "Basic $token")
        }
        contentType(ContentType.Application.Json)
    }

    private suspend inline fun <reified T> safeGet(
        path: String,
        credentials: Credentials?,
    ): ApiResult<T> {
        return try {
            val response = client.get("$baseUrl$path") {
                applyAuth(credentials)
            }
            if (response.status.isSuccess()) {
                ApiResult(value = response.body())
            } else {
                ApiResult(error = response.body<JsonElement>().toErrorMessage())
            }
        } catch (e: Exception) {
            ApiResult(error = e.message ?: "Error inesperado")
        }
    }

    private suspend inline fun <reified T, reified Body> safePost(
        path: String,
        body: Body,
        credentials: Credentials? = null,
    ): ApiResult<T> {
        return try {
            val response = client.post("$baseUrl$path") {
                applyAuth(credentials)
                setBody(body)
            }
            if (response.status.isSuccess()) {
                ApiResult(value = response.body())
            } else {
                ApiResult(error = response.body<JsonElement>().toErrorMessage())
            }
        } catch (e: Exception) {
            ApiResult(error = e.message ?: "Error inesperado")
        }
    }

    private suspend inline fun <reified T> safePost(
        path: String,
        credentials: Credentials? = null,
    ): ApiResult<T> {
        return try {
            val response = client.post("$baseUrl$path") {
                applyAuth(credentials)
            }
            if (response.status.isSuccess()) {
                ApiResult(value = response.body())
            } else {
                ApiResult(error = response.body<JsonElement>().toErrorMessage())
            }
        } catch (e: Exception) {
            ApiResult(error = e.message ?: "Error inesperado")
        }
    }

    private suspend inline fun <reified T> safeDelete(
        path: String,
        credentials: Credentials? = null,
    ): ApiResult<T> {
        return try {
            val response = client.delete("$baseUrl$path") {
                applyAuth(credentials)
            }
            if (response.status.isSuccess()) {
                ApiResult(value = response.body())
            } else {
                ApiResult(error = response.body<JsonElement>().toErrorMessage())
            }
        } catch (e: Exception) {
            ApiResult(error = e.message ?: "Error inesperado")
        }
    }

    suspend fun login(username: String, password: String): ApiResult<LoginResponse> {
        return safePost(
            path = "/api/auth/login",
            body = LoginRequest(username, password),
        )
    }

    suspend fun register(request: RegisterRequest): ApiResult<RegisterResponse> {
        return safePost(
            path = "/api/auth/register",
            body = request,
        )
    }

    suspend fun logout(credentials: Credentials): ApiResult<JsonElement> {
        return safePost("/api/auth/logout", credentials = credentials)
    }

    suspend fun getSession(credentials: Credentials): ApiResult<Sesion> {
        return safeGet("/api/sesion", credentials)
    }

    suspend fun updateStep(
        credentials: Credentials,
        paso: String,
        eventoId: Long? = null,
    ): ApiResult<Sesion> {
        val path = buildString {
            append("/api/sesion/paso?paso=")
            append(paso)
            if (eventoId != null) {
                append("&eventoId=")
                append(eventoId)
            }
        }
        return safePost(path = path, credentials = credentials)
    }

    suspend fun listEventos(credentials: Credentials): ApiResult<List<Evento>> {
        return safeGet("/api/eventos", credentials)
    }

    suspend fun getEventoDetalle(credentials: Credentials, eventoId: Long): ApiResult<EventoDetalleResponse> {
        return safeGet("/api/eventos/$eventoId", credentials)
    }

    suspend fun seleccionarAsientos(
        credentials: Credentials,
        request: SeleccionAsientosRequest,
    ): ApiResult<Sesion> {
        return safePost(
            path = "/api/sesion/seleccionar-asientos",
            body = request,
            credentials = credentials,
        )
    }

    suspend fun bloquearAsientos(credentials: Credentials): ApiResult<BloqueoResponse> {
        return safePost("/api/sesion/bloquear-asientos", credentials = credentials)
    }

    suspend fun realizarVenta(credentials: Credentials): ApiResult<VentaResponse> {
        return safePost("/api/ventas/realizar", credentials = credentials)
    }

    suspend fun listVentas(credentials: Credentials): ApiResult<List<Venta>> {
        return safeGet("/api/ventas", credentials)
    }

    suspend fun eliminarSesion(credentials: Credentials): ApiResult<JsonElement> {
        return safeDelete("/api/sesion", credentials)
    }

    private fun JsonElement.toErrorMessage(): String {
        if (this is JsonObject) {
            val message = this.jsonObject["message"]?.toString()?.trim('"')
            if (!message.isNullOrBlank()) return message
            val descripcion = this.jsonObject["descripcion"]?.toString()?.trim('"')
            if (!descripcion.isNullOrBlank()) return descripcion
        }
        return "Error inesperado"
    }
}

data class ApiResult<T>(
    val value: T? = null,
    val error: String? = null,
) {
    val isSuccess: Boolean
        get() = error == null
}
