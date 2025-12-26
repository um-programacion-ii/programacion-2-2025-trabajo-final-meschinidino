package org.eventos.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.eventos.project.data.ApiClient
import org.eventos.project.data.Credentials
import org.eventos.project.data.Evento
import org.eventos.project.data.EventoDetalleResponse
import org.eventos.project.data.SeatKey
import org.eventos.project.data.SeatSelection
import org.eventos.project.data.SeleccionAsientosRequest
import org.eventos.project.data.Sesion
import org.eventos.project.data.VentaResponse
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

private enum class Screen {
    LOGIN,
    EVENT_LIST,
    EVENT_DETAIL,
    SEAT_MAP,
    PASSENGER_INFO,
    CONFIRMATION,
    RESULT,
}

private enum class SeatStatus {
    FREE,
    SOLD,
    BLOCKED,
    SELECTED,
    UNKNOWN,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        val scope = rememberCoroutineScope()
        var baseUrl by remember { mutableStateOf(defaultBaseUrl()) }
        var credentials by remember { mutableStateOf<Credentials?>(null) }
        var screen by remember { mutableStateOf(Screen.LOGIN) }
        var events by remember { mutableStateOf<List<Evento>>(emptyList()) }
        var selectedEvent by remember { mutableStateOf<Evento?>(null) }
        var seatStatuses by remember { mutableStateOf<Map<SeatKey, SeatStatus>>(emptyMap()) }
        var selectedSeats by remember { mutableStateOf<List<SeatSelection>>(emptyList()) }
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

        fun clearSessionState() {
            selectedEvent = null
            seatStatuses = emptyMap()
            selectedSeats = emptyList()
            ventaResponse = null
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

        fun logout() {
            val userCreds = credentials ?: return
            setLoading(true)
            scope.launch {
                api.logout(userCreds)
                credentials = null
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

        fun goToSeatMap() {
            screen = Screen.SEAT_MAP
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
            screen = Screen.EVENT_LIST
            loadEventos()
        }

        Column(modifier = Modifier.fillMaxSize()) {
            if (credentials != null) {
                TopAppBar(
                    title = { Text("Sistema de Eventos") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    actions = {
                        Text(
                            text = "Salir",
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clickable { logout() },
                            color = MaterialTheme.colorScheme.primary,
                        )
                    },
                )
            }

            when (screen) {
                Screen.LOGIN -> LoginScreen(
                    baseUrl = baseUrl,
                    onBaseUrlChange = { baseUrl = it },
                    onLogin = { user, pass -> login(user, pass) },
                    isLoading = isLoading,
                    infoMessage = infoMessage,
                )
                Screen.EVENT_LIST -> EventListScreen(
                    events = events,
                    onEventClick = { selectEvent(it) },
                    onRefresh = { loadEventos() },
                    isLoading = isLoading,
                    infoMessage = infoMessage,
                )
                Screen.EVENT_DETAIL -> EventDetailScreen(
                    evento = selectedEvent,
                    onBack = { returnToList() },
                    onSeatMap = { goToSeatMap() },
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
            }
        }
    }
}

@Composable
private fun LoginScreen(
    baseUrl: String,
    onBaseUrlChange: (String) -> Unit,
    onLogin: (String, String) -> Unit,
    isLoading: Boolean,
    infoMessage: String?,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Ingreso",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = baseUrl,
            onValueChange = onBaseUrlChange,
            label = { Text("URL Backend") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contrasena") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onLogin(username, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
        ) {
            Text(if (isLoading) "Ingresando..." else "Iniciar sesion")
        }
        if (!infoMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = infoMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun EventListScreen(
    events: List<Evento>,
    onEventClick: (Evento) -> Unit,
    onRefresh: () -> Unit,
    isLoading: Boolean,
    infoMessage: String?,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Eventos activos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            OutlinedButton(onClick = onRefresh, enabled = !isLoading) {
                Text("Actualizar")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (!infoMessage.isNullOrBlank()) {
            Text(text = infoMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (events.isEmpty()) {
            Text(text = if (isLoading) "Cargando eventos..." else "No hay eventos disponibles")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(events) { evento ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEventClick(evento) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = evento.titulo,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = evento.resumen ?: "Sin resumen",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Fecha: ${evento.fecha ?: "Sin fecha"}",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventDetailScreen(
    evento: Evento?,
    onBack: () -> Unit,
    onSeatMap: () -> Unit,
    isLoading: Boolean,
    infoMessage: String?,
) {
    if (evento == null) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("No hay evento seleccionado")
        }
        return
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = onBack) {
                Text("Volver")
            }
            Button(onClick = onSeatMap, enabled = !isLoading) {
                Text("Ver asientos")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = evento.titulo,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = evento.descripcion ?: "Sin descripcion")
        Spacer(modifier = Modifier.height(12.dp))
        Divider()
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Fecha: ${evento.fecha ?: "Sin fecha"}")
        Text(text = "Direccion: ${evento.direccion ?: "Sin direccion"}")
        Text(text = "Precio: ${evento.precioEntrada ?: 0.0}")
        Text(text = "Capacidad: ${evento.filaAsientos ?: 0} x ${evento.columnaAsientos ?: 0}")
        if (!infoMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = infoMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun SeatMapScreen(
    evento: Evento?,
    seatStatuses: Map<SeatKey, SeatStatus>,
    selectedSeats: List<SeatSelection>,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onSeatToggle: (SeatKey) -> Unit,
    onContinue: () -> Unit,
    isLoading: Boolean,
    infoMessage: String?,
) {
    if (evento == null) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("No hay evento seleccionado")
        }
        return
    }

    val rows = evento.filaAsientos ?: 0
    val cols = evento.columnaAsientos ?: 0
    val seatList = buildList {
        for (row in 1..rows) {
            for (col in 1..cols) {
                add(SeatKey(row, col))
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = onBack) { Text("Volver") }
            OutlinedButton(onClick = onRefresh, enabled = !isLoading) { Text("Actualizar") }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Selecciona entre 1 y 4 asientos",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        SeatLegend()
        Spacer(modifier = Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(if (cols == 0) 1 else cols),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f, fill = true),
        ) {
            items(seatList) { seat ->
                val isSelected = selectedSeats.any { it.seat == seat }
                val rawStatus = seatStatuses[seat] ?: SeatStatus.FREE
                val status = if (isSelected) SeatStatus.SELECTED else rawStatus
                val isSelectable = status == SeatStatus.FREE || status == SeatStatus.SELECTED
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(seatColor(status), shape = MaterialTheme.shapes.small)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        .clickable(enabled = isSelectable) { onSeatToggle(seat) },
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Seleccionados: ${selectedSeats.size} / 4")
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
        ) {
            Text(if (isLoading) "Procesando..." else "Continuar")
        }
        if (!infoMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = infoMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun SeatLegend() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        LegendItem(label = "Libre", color = seatColor(SeatStatus.FREE))
        LegendItem(label = "Vendido", color = seatColor(SeatStatus.SOLD))
        LegendItem(label = "Bloqueado", color = seatColor(SeatStatus.BLOCKED))
        LegendItem(label = "Seleccionado", color = seatColor(SeatStatus.SELECTED))
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(color, shape = MaterialTheme.shapes.extraSmall),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun PassengerInfoScreen(
    selectedSeats: List<SeatSelection>,
    onBack: () -> Unit,
    onUpdatePassenger: (Int, String) -> Unit,
    onContinue: () -> Unit,
    isLoading: Boolean,
    infoMessage: String?,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        OutlinedButton(onClick = onBack) { Text("Volver") }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Datos de los pasajeros",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(12.dp))
        selectedSeats.forEachIndexed { index, selection ->
            Text(text = "Asiento F${selection.seat.fila} C${selection.seat.columna}")
            OutlinedTextField(
                value = selection.persona,
                onValueChange = { onUpdatePassenger(index, it) },
                label = { Text("Nombre y apellido") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
        ) {
            Text(if (isLoading) "Guardando..." else "Continuar")
        }
        if (!infoMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = infoMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun ConfirmationScreen(
    evento: Evento?,
    selectedSeats: List<SeatSelection>,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    isLoading: Boolean,
    infoMessage: String?,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        OutlinedButton(onClick = onBack) { Text("Volver") }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Confirmacion de compra",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Evento: ${evento?.titulo ?: "-"}")
        Spacer(modifier = Modifier.height(8.dp))
        selectedSeats.forEach { selection ->
            Text(text = "F${selection.seat.fila} C${selection.seat.columna} - ${selection.persona}")
        }
        Spacer(modifier = Modifier.height(12.dp))
        val price = (evento?.precioEntrada ?: 0.0) * selectedSeats.size
        Text(text = "Total: $price")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        ) {
            Text(if (isLoading) "Confirmando..." else "Comprar")
        }
        if (!infoMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = infoMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun ResultScreen(
    ventaResponse: VentaResponse?,
    onBackToList: () -> Unit,
) {
    val success = ventaResponse?.success == true
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (success) "Compra confirmada" else "Compra rechazada",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = if (success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = ventaResponse?.descripcion ?: ventaResponse?.message ?: "Sin detalle")
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onBackToList) {
            Text("Volver al listado")
        }
    }
}

private fun seatColor(status: SeatStatus): Color {
    return when (status) {
        SeatStatus.FREE -> Color(0xFF2E7D32)
        SeatStatus.SOLD -> Color(0xFFC62828)
        SeatStatus.BLOCKED -> Color(0xFF757575)
        SeatStatus.SELECTED -> Color(0xFF1565C0)
        SeatStatus.UNKNOWN -> Color(0xFFBDBDBD)
    }
}

private fun parseSeatStatuses(
    asientos: JsonElement?,
    rows: Int,
    cols: Int,
): Map<SeatKey, SeatStatus> {
    if (asientos !is JsonObject) return emptyMap()
    val regex = Regex("fila:(\\d+):columna:(\\d+)")
    val result = mutableMapOf<SeatKey, SeatStatus>()
    for ((key, value) in asientos) {
        val match = regex.find(key) ?: continue
        val row = match.groupValues[1].toIntOrNull() ?: continue
        val col = match.groupValues[2].toIntOrNull() ?: continue
        if (row < 1 || col < 1 || row > rows || col > cols) continue
        val status = mapStatus(value.jsonPrimitive.content)
        result[SeatKey(row, col)] = status
    }
    return result
}

private fun mapStatus(value: String?): SeatStatus {
    val normalized = value?.lowercase() ?: return SeatStatus.UNKNOWN
    return when {
        "vend" in normalized || "ocup" in normalized -> SeatStatus.SOLD
        "bloq" in normalized || "reserv" in normalized -> SeatStatus.BLOCKED
        "libre" in normalized -> SeatStatus.FREE
        else -> SeatStatus.UNKNOWN
    }
}
