package com.ufps.registrousuarios.ui.registro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ufps.registrousuarios.data.remote.RegistroResponse
import com.ufps.registrousuarios.ui.theme.RegistroUsuariosTheme

@Composable
fun RegistroScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistroViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RegistroContent(
        state = state,
        onServidorChange = viewModel::onServidorChange,
        onNombreChange = viewModel::onNombreChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onRegistrar = viewModel::registrar,
        onRegistrarOtro = viewModel::registrarOtro,
        modifier = modifier
    )
}

@Composable
fun RegistroContent(
    state: RegistroState,
    onServidorChange: (String) -> Unit,
    onNombreChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegistrar: () -> Unit,
    onRegistrarOtro: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "Android + Retrofit  →  POST /api/auth/register",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

        val exito = state.registroExitoso
        if (exito != null) {
            ExitoCard(exito)
            Button(onClick = onRegistrarOtro, modifier = Modifier.fillMaxWidth()) {
                Text("Registrar otro usuario")
            }
        } else {
            Formulario(state, onServidorChange, onNombreChange, onEmailChange, onPasswordChange, onRegistrar)
        }
    }
}

@Composable
private fun Formulario(
    state: RegistroState,
    onServidorChange: (String) -> Unit,
    onNombreChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegistrar: () -> Unit
) {
    var verPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = state.servidor,
        onValueChange = onServidorChange,
        label = { Text("Servidor (IP del backend)") },
        supportingText = { Text("Emulador: 10.0.2.2 · Celular: IP del PC con el backend") },
        singleLine = true,
        enabled = !state.isLoading,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = state.nombre,
        onValueChange = onNombreChange,
        label = { Text("Nombre") },
        singleLine = true,
        enabled = !state.isLoading,
        isError = "nombre" in state.erroresCampo,
        supportingText = state.erroresCampo["nombre"]?.let { { Text(it) } },
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = state.email,
        onValueChange = onEmailChange,
        label = { Text("Correo") },
        singleLine = true,
        enabled = !state.isLoading,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = "email" in state.erroresCampo,
        supportingText = state.erroresCampo["email"]?.let { { Text(it) } },
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = state.password,
        onValueChange = onPasswordChange,
        label = { Text("Contraseña (mínimo 8 caracteres)") },
        singleLine = true,
        enabled = !state.isLoading,
        visualTransformation = if (verPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            TextButton(onClick = { verPassword = !verPassword }) {
                Text(if (verPassword) "Ocultar" else "Ver")
            }
        },
        isError = "password" in state.erroresCampo,
        supportingText = state.erroresCampo["password"]?.let { { Text(it) } },
        modifier = Modifier.fillMaxWidth()
    )

    state.error?.let { ErrorCard(it) }

    Button(
        onClick = onRegistrar,
        enabled = !state.isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        if (state.isLoading) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text("Enviando…")
            }
        } else {
            Text("Crear Cuenta")
        }
    }
}

@Composable
private fun ErrorCard(mensaje: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            mensaje,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun ExitoCard(usuario: RegistroResponse) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                usuario.mensaje,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(4.dp))
            Dato("Respuesta", "HTTP 201 Created")
            Dato("ID", usuario.id.toString())
            Dato("Nombre", usuario.nombre)
            Dato("Correo", usuario.email)
        }
    }
}

@Composable
private fun Dato(etiqueta: String, valor: String) {
    Row {
        Text(
            "$etiqueta: ",
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(valor, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Preview(showBackground = true)
@Composable
private fun FormularioPreview() {
    RegistroUsuariosTheme(dynamicColor = false) {
        RegistroContent(
            state = RegistroState(
                nombre = "Ana",
                email = "ana",
                erroresCampo = mapOf("email" to "El email no tiene un formato valido")
            ),
            onServidorChange = {}, onNombreChange = {}, onEmailChange = {}, onPasswordChange = {},
            onRegistrar = {}, onRegistrarOtro = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExitoPreview() {
    RegistroUsuariosTheme(dynamicColor = false) {
        RegistroContent(
            state = RegistroState(
                registroExitoso = RegistroResponse(1, "Ana", "ana@ufps.edu.co", "Usuario registrado correctamente")
            ),
            onServidorChange = {}, onNombreChange = {}, onEmailChange = {}, onPasswordChange = {},
            onRegistrar = {}, onRegistrarOtro = {}
        )
    }
}
