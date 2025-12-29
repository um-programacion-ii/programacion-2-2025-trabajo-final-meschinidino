package org.eventos.project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.eventos.project.data.Evento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    evento: Evento?,
    onBack: () -> Unit,
    onSeatMap: () -> Unit,
    isLoading: Boolean,
    infoMessage: String?,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(evento?.titulo ?: "Detalle del Evento") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (evento == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Evento no encontrado")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = evento.descripcion ?: "Sin descripción disponible.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow("Fecha", evento.fecha?.replace("T", " ") ?: "-")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    DetailRow("Lugar", evento.direccion ?: "-")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    DetailRow("Precio Entrada", "$${evento.precioEntrada ?: 0.0}")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSeatMap,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading
            ) {
                Text("Seleccionar Asientos", style = MaterialTheme.typography.labelLarge)
            }
            
            if (!infoMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = infoMessage, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
