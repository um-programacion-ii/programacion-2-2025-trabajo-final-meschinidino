package org.eventos.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.eventos.project.data.ApiClient
import org.eventos.project.data.Credentials
import org.eventos.project.data.Evento
import org.eventos.project.data.EventoDetalleResponse
import org.eventos.project.data.RegisterRequest
import org.eventos.project.data.SeatKey
import org.eventos.project.data.SeatSelection
import org.eventos.project.data.SeleccionAsientosRequest
import org.eventos.project.data.Sesion
import org.eventos.project.data.Venta
import org.eventos.project.data.VentaResponse
import org.eventos.project.ui.screens.*
import org.eventos.project.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

private enum class Screen {
    LOGIN,
    EVENT_LIST,
    EVENT_DETAIL,
    SEAT_MAP,
    PASSENGER_INFO,
    CONFIRMATION,
    RESULT,
    PURCHASES,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    AppTheme {
        val scope = rememberCoroutineScope()
        val isAndroid = remember { getPlatform().name.startsWith("Android") }
        var baseUrl by remember { mutableStateOf(defaultBaseUrl()) }
        var credentials by remember { mutableStateOf<Credentials?>(null) }
        var screen by remember { mutableStateOf(Screen.LOGIN) }
        var authMode by remember { mutableStateOf(AuthMode.LOGIN) }
        var authPrefillUsername by remember { mutableStateOf("") }
        var authPrefillPassword by remember { mutableStateOf("") }
        var events by remember { mutableStateOf<List<Evento>>(emptyList()) }
        var selectedEvent by remember { mutableStateOf<Evento?>(null) }
        var seatStatuses by remember { mutableStateOf<Map<SeatKey, SeatStatus>>(emptyMap()) }
        var selectedSeats by remember { mutableStateOf<List<SeatSelection>>(emptyList()) }
        var ventas by remember { mutableStateOf<List<Venta>>(emptyList()) }
        var ventaResponse by remember { mutableStateOf<VentaResponse?>(null) }
        var infoMessage by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }

        val api = remember(baseUrl) { ApiClient(baseUrl) }

        fun setLoading(loading: Boolean) {
            isLoading = loading
        }

        fun setMessage(message: String?) {
            infoMessage = message
        }

        fun setAuthMode(mode: AuthMode) {
            authMode = mode
            setMessage(null)
        }

        fun clearSessionState() {
            selectedEvent = null
            seatStatuses = emptyMap()
            selectedSeats = emptyList()
        }

        fun updateSelectedSeatsFromSession(session: Sesion) {
            selectedSeats = session.asientosSeleccionados.map {
                SeatSelection(seat = SeatKey(it.fila, it.columna), persona = it.persona ?: "")
            }
        }

        fun resolveScreenFromSession(session: Sesion) {
            val paso = session.paso ?: "LISTADO_EVENTOS"
            when (paso) {
                "LISTADO_EVENTOS" -> screen = Screen.EVENT_LIST
                "DETALLE_EVENTO" -> screen = Screen.EVENT_DETAIL
                "SELECCION_ASIENTOS" -> screen = Screen.SEAT_MAP
                "CARGA_DATOS" -> screen = Screen.PASSENGER_INFO
                "VENTA" -> screen = Screen.CONFIRMATION
                else -> screen = Screen.EVENT_LIST
            }
        }

        fun loadEventos() {
            val userCreds = credentials ?: return
            setLoading(true)
            scope.launch {
                val response = api.listEventos(userCreds)
                if (response.isSuccess) {
                    events = response.value.orEmpty()
                } else {
                    setMessage(response.error)
                }
                setLoading(false)
            }
        }

        fun loadEventoDetalle(eventoId: Long, onLoaded: (EventoDetalleResponse) -> Unit) {
            val userCreds = credentials ?: return
            setLoading(true)
            scope.launch {
                val response = api.getEventoDetalle(userCreds, eventoId)
                if (response.isSuccess && response.value != null) {
                    onLoaded(response.value)
                } else {
                    setMessage(response.error)
                }
                setLoading(false)
            }
        }

        fun refreshSeatStatuses(detail: EventoDetalleResponse) {
            val event = detail.evento
            selectedEvent = event
            val rows = event.filaAsientos ?: 0
            val cols = event.columnaAsientos ?: 0
            seatStatuses = parseSeatStatuses(detail.asientos, rows, cols)
        }

        fun handleSession(session: Sesion) {
            if (session.eventoId != null) {
                loadEventoDetalle(session.eventoId) { detail ->
                    refreshSeatStatuses(detail)
                    updateSelectedSeatsFromSession(session)
                    resolveScreenFromSession(session)
                }
            } else {
                resolveScreenFromSession(session)
                loadEventos()
            }
        }

        fun login(username: String, password: String) {
            setLoading(true)
            setMessage(null)
            scope.launch {
                val response = api.login(username, password)
                if (response.isSuccess && response.value?.success == true) {
                    credentials = Credentials(username, password)
                    val sessionResponse = api.getSession(credentials!!)
                    if (sessionResponse.isSuccess && sessionResponse.value != null) {
                        handleSession(sessionResponse.value)
                    } else {
                        screen = Screen.EVENT_LIST
                        loadEventos()
                        setMessage(sessionResponse.error)
                    }
                } else {
                    setMessage(response.value?.message ?: response.error)
                }
                setLoading(false)
            }
        }

        fun register(
            username: String,
            password: String,
            email: String,
            firstName: String,
            lastName: String,
        ) {
            if (username.isBlank() || password.isBlank() || email.isBlank() ||
                firstName.isBlank() || lastName.isBlank()
            ) {
                setMessage("Completa todos los campos para registrarte")
                return
            }
            setLoading(true)
            setMessage(null)
            scope.launch {
                val response = api.register(
                    RegisterRequest(
                        username = username.trim(),
                        password = password,
                        email = email.trim(),
                        firstName = firstName.trim(),
                        lastName = lastName.trim(),
                    )
                )
                if (response.isSuccess && response.value?.success == true) {
                    authPrefillUsername = username
                    authPrefillPassword = password
                    setAuthMode(AuthMode.LOGIN)
                    setMessage("Cuenta creada. Inicia sesión para continuar.")
                } else {
                    setMessage(response.value?.message ?: response.error)
                }
                setLoading(false)
            }
        }

        fun logout() {
            val userCreds = credentials ?: return
            setLoading(true)
            scope.launch {
                api.logout(userCreds)
                credentials = null
                ventaResponse = null
                clearSessionState()
                screen = Screen.LOGIN
                setLoading(false)
            }
        }

        fun selectEvent(evento: Evento) {
            val userCreds = credentials ?: return
            setLoading(true)
            scope.launch {
                api.updateStep(userCreds, "DETALLE_EVENTO", evento.id)
                val response = api.getEventoDetalle(userCreds, evento.id)
                if (response.isSuccess && response.value != null) {
                    refreshSeatStatuses(response.value)
                    screen = Screen.EVENT_DETAIL
                } else {
                    setMessage(response.error)
                }
                setLoading(false)
            }
        }

        fun returnToList() {
            val userCreds = credentials
            if (userCreds != null) {
                scope.launch {
                    api.updateStep(userCreds, "LISTADO_EVENTOS")
                }
            }
            screen = Screen.EVENT_LIST
            loadEventos()
        }

        fun refreshSeats() {
            val event = selectedEvent ?: return
            loadEventoDetalle(event.id) { detail ->
                refreshSeatStatuses(detail)
            }
        }

        fun toggleSeat(seat: SeatKey) {
            val event = selectedEvent ?: return
            val totalSeats = (event.filaAsientos ?: 0) * (event.columnaAsientos ?: 0)
            if (totalSeats == 0) return
            val current = selectedSeats.toMutableList()
            val existingIndex = current.indexOfFirst { it.seat == seat }
            if (existingIndex >= 0) {
                current.removeAt(existingIndex)
                selectedSeats = current
                return
            }
            if (current.size >= 4) {
                setMessage("Solo se permiten hasta 4 asientos")
                return
            }
            current.add(SeatSelection(seat = seat, persona = ""))
            selectedSeats = current
        }

        fun submitSeatSelection() {
            val userCreds = credentials ?: return
            val event = selectedEvent ?: return
            if (selectedSeats.isEmpty()) {
                setMessage("Selecciona al menos 1 asiento")
                return
            }
            setLoading(true)
            scope.launch {
                val request = SeleccionAsientosRequest(
                    eventoId = event.id,
                    asientos = selectedSeats.map {
                        SeleccionAsientosRequest.AsientoSeleccionado(
                            fila = it.seat.fila,
                            columna = it.seat.columna,
                            persona = it.persona.ifBlank { null },
                        )
                    },
                )
                val selectionResponse = api.seleccionarAsientos(userCreds, request)
                if (selectionResponse.isSuccess) {
                    val bloqueoResult = api.bloquearAsientos(userCreds)
                    if (bloqueoResult.isSuccess && bloqueoResult.value?.success == true) {
                        screen = Screen.PASSENGER_INFO
                    } else {
                        setMessage(bloqueoResult.value?.descripcion ?: bloqueoResult.error)
                        refreshSeats()
                    }
                } else {
                    setMessage(selectionResponse.error)
                }
                setLoading(false)
            }
        }

        fun loadVentas() {
            val userCreds = credentials ?: return
            setLoading(true)
            scope.launch {
                val response = api.listVentas(userCreds)
                if (response.isSuccess) {
                    ventas = response.value.orEmpty()
                } else {
                    setMessage(response.error)
                }
                setLoading(false)
            }
        }
        
        fun updatePassengerInfo(index: Int, persona: String) {
            val current = selectedSeats.toMutableList()
            val seat = current[index]
            current[index] = seat.copy(persona = persona)
            selectedSeats = current
        }

        fun goToConfirmation() {
            val userCreds = credentials ?: return
            val event = selectedEvent ?: return
            if (selectedSeats.any { it.persona.isBlank() }) {
                setMessage("Completa el nombre y apellido para cada asiento")
                return
            }
            setLoading(true)
            scope.launch {
                val request = SeleccionAsientosRequest(
                    eventoId = event.id,
                    asientos = selectedSeats.map {
                        SeleccionAsientosRequest.AsientoSeleccionado(
                            fila = it.seat.fila,
                            columna = it.seat.columna,
                            persona = it.persona,
                        )
                    },
                )
                val updateResponse = api.seleccionarAsientos(userCreds, request)
                if (updateResponse.isSuccess) {
                    api.updateStep(userCreds, "VENTA", event.id)
                    screen = Screen.CONFIRMATION
                } else {
                    setMessage(updateResponse.error)
                }
                setLoading(false)
            }
        }

        fun confirmarVenta() {
            val userCreds = credentials ?: return
            setLoading(true)
            scope.launch {
                val response = api.realizarVenta(userCreds)
                ventaResponse = response.value
                if (response.isSuccess) {
                    screen = Screen.RESULT
                    clearSessionState()
                } else {
                    setMessage(response.error)
                }
                setLoading(false)
            }
        }

        fun startOver() {
            clearSessionState()
            ventaResponse = null
            screen = Screen.EVENT_LIST
            loadEventos()
        }

        Column(modifier = Modifier.fillMaxSize()) {
            if (credentials != null) {
                TopAppBar(
                    title = { Text("Sistema de Eventos", style = MaterialTheme.typography.titleMedium) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    actions = {
                        Text(
                            text = "Salir",
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clickable { logout() },
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                )
            }

            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                when (screen) {
                    Screen.LOGIN -> AuthScreen(
                        authMode = authMode,
                        onAuthModeChange = { setAuthMode(it) },
                        baseUrl = baseUrl,
                        onBaseUrlChange = { baseUrl = it },
                        onLogin = { user, pass -> login(user, pass) },
                        onRegister = { user, pass, email, first, last ->
                            register(user, pass, email, first, last)
                        },
                        isLoading = isLoading,
                        infoMessage = infoMessage,
                        isAndroid = isAndroid,
                        prefillUsername = authPrefillUsername,
                        prefillPassword = authPrefillPassword,
                    )
                    Screen.EVENT_LIST -> EventListScreen(
                        events = events,
                        onEventClick = { selectEvent(it) },
                        onRefresh = { loadEventos() },
                        onPurchases = {
                            screen = Screen.PURCHASES
                            loadVentas()
                        },
                        isLoading = isLoading,
                        infoMessage = infoMessage,
                    )
                    Screen.EVENT_DETAIL -> EventDetailScreen(
                        evento = selectedEvent,
                        onBack = { returnToList() },
                        onSeatMap = { screen = Screen.SEAT_MAP },
                        isLoading = isLoading,
                        infoMessage = infoMessage,
                    )
                    Screen.SEAT_MAP -> SeatMapScreen(
                        evento = selectedEvent,
                        seatStatuses = seatStatuses,
                        selectedSeats = selectedSeats,
                        onBack = { screen = Screen.EVENT_DETAIL },
                        onRefresh = { refreshSeats() },
                        onSeatToggle = { toggleSeat(it) },
                        onContinue = { submitSeatSelection() },
                        isLoading = isLoading,
                        infoMessage = infoMessage,
                    )
                    Screen.PASSENGER_INFO -> PassengerInfoScreen(
                        selectedSeats = selectedSeats,
                        onBack = { screen = Screen.SEAT_MAP },
                        onUpdatePassenger = { index, persona -> updatePassengerInfo(index, persona) },
                        onContinue = { goToConfirmation() },
                        isLoading = isLoading,
                        infoMessage = infoMessage,
                    )
                    Screen.CONFIRMATION -> ConfirmationScreen(
                        evento = selectedEvent,
                        selectedSeats = selectedSeats,
                        onBack = { screen = Screen.PASSENGER_INFO },
                        onConfirm = { confirmarVenta() },
                        isLoading = isLoading,
                        infoMessage = infoMessage,
                    )
                    Screen.RESULT -> ResultScreen(
                        ventaResponse = ventaResponse,
                        onBackToList = { startOver() },
                    )
                    Screen.PURCHASES -> PurchasesScreen(
                        ventas = ventas,
                        onBack = {
                            screen = Screen.EVENT_LIST
                            loadEventos()
                        },
                        onRefresh = { loadVentas() },
                        isLoading = isLoading,
                        infoMessage = infoMessage,
                    )
                }
            }
        }
    }
}

private fun parseSeatStatuses(
    asientos: JsonElement?,
    rows: Int,
    cols: Int,
): Map<SeatKey, SeatStatus> {
    if (asientos == null) return emptyMap()

    return when (asientos) {
        is JsonArray -> parseSeatArray(asientos, rows, cols)
        is JsonObject -> {
            val asientosArray = asientos["asientos"]
            if (asientosArray is JsonArray) {
                parseSeatArray(asientosArray, rows, cols)
            } else {
                parseSeatMap(asientos, rows, cols)
            }
        }
        else -> emptyMap()
    }
}

private fun parseSeatMap(
    asientos: JsonObject,
    rows: Int,
    cols: Int,
): Map<SeatKey, SeatStatus> {
    val regex = Regex("fila:(\\d+):columna:(\\d+)")
    val result = mutableMapOf<SeatKey, SeatStatus>()
    for ((key, value) in asientos) {
        val match = regex.find(key) ?: continue
        val row = match.groupValues[1].toIntOrNull() ?: continue
        val col = match.groupValues[2].toIntOrNull() ?: continue
        if (row < 1 || col < 1 || row > rows || col > cols) continue
        result[SeatKey(row, col)] = resolveSeatStatus(value)
    }
    return result
}

private fun parseSeatArray(
    asientos: JsonArray,
    rows: Int,
    cols: Int,
): Map<SeatKey, SeatStatus> {
    val result = mutableMapOf<SeatKey, SeatStatus>()
    for (element in asientos) {
        val seatObj = element as? JsonObject ?: continue
        val row = seatObj["fila"]?.jsonPrimitive?.content?.toIntOrNull() ?: continue
        val col = seatObj["columna"]?.jsonPrimitive?.content?.toIntOrNull() ?: continue
        if (row < 1 || col < 1 || row > rows || col > cols) continue
        val estado = seatObj["estado"]?.jsonPrimitive?.content
        result[SeatKey(row, col)] = resolveSeatStatus(estado)
    }
    return result
}

private fun resolveSeatStatus(value: JsonElement?): SeatStatus {
    val status = when (value) {
        is JsonObject -> value["estado"]?.jsonPrimitive?.content
        else -> value?.jsonPrimitive?.content
    }
    return resolveSeatStatus(status)
}

private fun resolveSeatStatus(status: String?): SeatStatus {
    val statusStr = status?.lowercase().orEmpty()
    return when {
        "vend" in statusStr || "ocup" in statusStr -> SeatStatus.SOLD
        "bloq" in statusStr || "reserv" in statusStr -> SeatStatus.BLOCKED
        "libre" in statusStr -> SeatStatus.FREE
        statusStr.isBlank() -> SeatStatus.FREE
        else -> SeatStatus.UNKNOWN
    }
}
