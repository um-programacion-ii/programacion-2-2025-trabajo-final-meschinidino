package org.eventos.project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.eventos.project.data.Evento
import org.eventos.project.data.SeatSelection
import org.eventos.project.data.Venta
import org.eventos.project.data.VentaResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengerInfoScreen(
    selectedSeats: List<SeatSelection>,
    onBack: () -> Unit,
    onUpdatePassenger: (Int, String) -> Unit,
    onContinue: () -> Unit,
    isLoading: Boolean,
    infoMessage: String?,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Datos de Pasajeros") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Ingresa el nombre completo para cada asiento.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            selectedSeats.forEachIndexed { index, selection ->
                OutlinedTextField(
                    value = selection.persona,
                    onValueChange = { onUpdatePassenger(index, it) },
                    label = { Text("Asiento F${selection.seat.fila} C${selection.seat.columna}") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (!infoMessage.isNullOrBlank()) {
                Text(text = infoMessage, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                enabled = !isLoading,
            ) {
                Text(if (isLoading) "Guardando..." else "Continuar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    evento: Evento?,
    selectedSeats: List<SeatSelection>,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    isLoading: Boolean,
    infoMessage: String?,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar Compra") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Evento", style = MaterialTheme.typography.labelMedium)
                    Text(evento?.titulo ?: "-", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("Entradas", style = MaterialTheme.typography.labelMedium)
                    selectedSeats.forEach { selection ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("F${selection.seat.fila} C${selection.seat.columna}", style = MaterialTheme.typography.bodyMedium)
                            Text(selection.persona, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val pricePer = evento?.precioEntrada ?: 0.0
                    val total = pricePer * selectedSeats.size
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", style = MaterialTheme.typography.titleLarge)
                        Text("$${total}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            if (!infoMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = infoMessage, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
            ) {
                Text(if (isLoading) "Confirmando..." else "Confirmar Compra")
            }
        }
    }
}

@Composable
fun ResultScreen(
    ventaResponse: VentaResponse?,
    onBackToList: () -> Unit,
) {
    val success = ventaResponse?.success == true
    val color = if (success) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
    val icon = if (success) Icons.Default.CheckCircle else Icons.Default.Warning

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = color
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if (success) "¡Compra Exitosa!" else "Error en la Compra",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = ventaResponse?.descripcion ?: ventaResponse?.message ?: "Sin detalle",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onBackToList) {
            Text("Volver al Inicio")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchasesScreen(
    ventas: List<Venta>,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    isLoading: Boolean,
    infoMessage: String?,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Compras") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh, enabled = !isLoading) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (!infoMessage.isNullOrBlank()) {
                Text(text = infoMessage, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (ventas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No has realizado compras aún.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(ventas) { venta ->
                        PurchaseItem(venta)
                    }
                }
            }
        }
    }
}

@Composable
fun PurchaseItem(venta: Venta) {
    val approved = venta.resultado == true
    val color = if (approved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Venta #${venta.id ?: "-"}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = if (approved) "Confirmada" else "Rechazada",
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Evento ID: ${venta.eventoId}")
            Text("Fecha: ${venta.fechaVenta?.replace("T", " ") ?: "-"}")
            if (venta.precioVenta != null) {
                Text("Total: $${venta.precioVenta}", fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Asientos: ${venta.asientos.size}")
            venta.asientos.forEach { 
                Text("   • F${it.fila} C${it.columna} (${it.persona ?: "-"})", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
