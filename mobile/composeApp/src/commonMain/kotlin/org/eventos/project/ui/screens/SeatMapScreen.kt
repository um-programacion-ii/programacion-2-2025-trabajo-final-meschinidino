package org.eventos.project.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.eventos.project.data.Evento
import org.eventos.project.data.SeatKey
import org.eventos.project.data.SeatSelection

enum class SeatStatus {
    FREE,
    SOLD,
    BLOCKED,
    SELECTED,
    UNKNOWN,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatMapScreen(
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selección de Asientos") },
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
        if (evento == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay evento seleccionado")
            }
            return@Scaffold
        }

        val rows = evento.filaAsientos ?: 0
        val cols = evento.columnaAsientos ?: 0

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (rows == 0 || cols == 0) {
                Text(
                    "Configuración de asientos no disponible.",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(
                    text = "${evento.titulo}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Screen (Stage) Indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(8.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(50))
                )
                Text("Pantalla / Escenario", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                
                Spacer(modifier = Modifier.height(24.dp))

                val seatList = buildList {
                    for (row in 1..rows) {
                        for (col in 1..cols) {
                            add(SeatKey(row, col))
                        }
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(cols),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(seatList) { seat ->
                        val isSelected = selectedSeats.any { it.seat == seat }
                        val rawStatus = seatStatuses[seat] ?: SeatStatus.FREE
                        val status = if (isSelected) SeatStatus.SELECTED else rawStatus
                        // If we have no statuses at all, and it's free, it works.
                        // But if we are "not syncing", rawStatus will be FREE.
                        
                        SeatItem(
                            status = status,
                            onClick = { onSeatToggle(seat) },
                            modifier = Modifier.aspectRatio(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            SeatLegend()
            Spacer(modifier = Modifier.height(16.dp))
            
            if (!infoMessage.isNullOrBlank()) {
                Text(text = infoMessage, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && selectedSeats.isNotEmpty()
            ) {
                Text(if (isLoading) "Procesando..." else "Continuar (${selectedSeats.size})")
            }
        }
    }
}

@Composable
fun SeatItem(
    status: SeatStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = when (status) {
        SeatStatus.FREE -> Color(0xFF4CAF50) // Green
        SeatStatus.SOLD -> Color(0xFFE57373) // Red
        SeatStatus.BLOCKED -> Color(0xFFB0BEC5) // Grey
        SeatStatus.SELECTED -> MaterialTheme.colorScheme.primary
        SeatStatus.UNKNOWN -> Color.Gray
    }
    
    val isEnabled = status == SeatStatus.FREE || status == SeatStatus.SELECTED

    Box(
        modifier = modifier
            .clickable(enabled = isEnabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Draw a simple chair shape
        Canvas(modifier = Modifier.fillMaxSize(0.8f)) {
            val path = Path().apply {
                // Chair Seat
                moveTo(0f, size.height * 0.4f)
                lineTo(size.width, size.height * 0.4f)
                lineTo(size.width * 0.9f, size.height * 0.8f)
                lineTo(size.width * 0.1f, size.height * 0.8f)
                close()
                // Chair Back
                moveTo(size.width * 0.15f, size.height * 0.4f)
                lineTo(size.width * 0.15f, 0f)
                lineTo(size.width * 0.85f, 0f)
                lineTo(size.width * 0.85f, size.height * 0.4f)
            }
            
            drawPath(path, color = color, style = Fill)
            
            // Armrests (optional)
            /*
            drawRoundRect(
                color = color,
                topLeft = Offset(0f, size.height * 0.4f),
                size = Size(size.width * 0.1f, size.height * 0.4f),
                cornerRadius = CornerRadius(2f)
            )
            drawRoundRect(
                color = color,
                topLeft = Offset(size.width * 0.9f, size.height * 0.4f),
                size = Size(size.width * 0.1f, size.height * 0.4f),
                cornerRadius = CornerRadius(2f)
            )
             */
        }
    }
}

@Composable
private fun SeatLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(label = "Libre", color = Color(0xFF4CAF50))
        LegendItem(label = "Vendido", color = Color(0xFFE57373))
        LegendItem(label = "Bloqueado", color = Color(0xFFB0BEC5))
        LegendItem(label = "Tu selección", color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}
