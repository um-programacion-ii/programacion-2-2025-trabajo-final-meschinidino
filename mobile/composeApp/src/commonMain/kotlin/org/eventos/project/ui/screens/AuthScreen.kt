package org.eventos.project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

enum class AuthMode {
    LOGIN,
    REGISTER,
}

@Composable
fun AuthScreen(
    authMode: AuthMode,
    onAuthModeChange: (AuthMode) -> Unit,
    baseUrl: String,
    onBaseUrlChange: (String) -> Unit,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String, String, String) -> Unit,
    isLoading: Boolean,
    infoMessage: String?,
    isAndroid: Boolean,
    prefillUsername: String,
    prefillPassword: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth().widthIn(max = 400.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bienvenido",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Sistema de Eventos", style = MaterialTheme.typography.titleMedium)
                
                if (!isAndroid) {
                    OutlinedTextField(
                        value = baseUrl,
                        onValueChange = onBaseUrlChange,
                        label = { Text("URL Backend") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Custom Toggle using Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val loginColor = if (authMode == AuthMode.LOGIN) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
                    Button(
                        onClick = { onAuthModeChange(AuthMode.LOGIN) },
                        modifier = Modifier.weight(1f),
                        colors = if (authMode == AuthMode.LOGIN) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("Ingresar")
                    }
                    Button(
                        onClick = { onAuthModeChange(AuthMode.REGISTER) },
                        modifier = Modifier.weight(1f),
                        colors = if (authMode == AuthMode.REGISTER) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("Registro")
                    }
                }

                when (authMode) {
                    AuthMode.LOGIN -> LoginForm(
                        onLogin = onLogin,
                        isLoading = isLoading,
                        prefillUsername = prefillUsername,
                        prefillPassword = prefillPassword,
                    )
                    AuthMode.REGISTER -> RegisterForm(
                        onRegister = onRegister,
                        isLoading = isLoading,
                    )
                }
                
                if (!infoMessage.isNullOrBlank()) {
                    Text(
                        text = infoMessage, 
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginForm(
    onLogin: (String, String) -> Unit,
    isLoading: Boolean,
    prefillUsername: String,
    prefillPassword: String,
) {
    var username by remember(prefillUsername) { mutableStateOf(prefillUsername) }
    var password by remember(prefillPassword) { mutableStateOf(prefillPassword) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
        Button(
            onClick = { onLogin(username, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Iniciar sesión")
        }
    }
}

@Composable
private fun RegisterForm(
    onRegister: (String, String, String, String, String) -> Unit,
    isLoading: Boolean,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombre") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellido") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onRegister(username, password, email, firstName, lastName) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Crear cuenta")
        }
    }
}
