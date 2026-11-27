package com.ufps.registrousuarios.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ufps.registrousuarios.data.RegistroRepository
import com.ufps.registrousuarios.data.ResultadoRegistro
import com.ufps.registrousuarios.data.remote.RegistroRequest
import com.ufps.registrousuarios.data.remote.RegistroResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistroState(
    /** IP o URL del backend. En emulador 10.0.2.2; en celular, la IP del PC. */
    val servidor: String = "10.0.2.2",
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    /** Mensaje general (409, sin conexion, 5xx). */
    val error: String? = null,
    /** Errores por campo que vienen del 400 del backend. */
    val erroresCampo: Map<String, String> = emptyMap(),
    val registroExitoso: RegistroResponse? = null
)

class RegistroViewModel(
    private val repository: RegistroRepository = RegistroRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(RegistroState())
    val state: StateFlow<RegistroState> = _state.asStateFlow()

    fun onServidorChange(value: String) = _state.update { it.copy(servidor = value, error = null) }

    fun onNombreChange(value: String) = _state.update {
        it.copy(nombre = value, erroresCampo = it.erroresCampo - "nombre", error = null)
    }

    fun onEmailChange(value: String) = _state.update {
        it.copy(email = value, erroresCampo = it.erroresCampo - "email", error = null)
    }

    fun onPasswordChange(value: String) = _state.update {
        it.copy(password = value, erroresCampo = it.erroresCampo - "password", error = null)
    }

    fun registrar() {
        val actual = _state.value
        if (actual.isLoading) return
        _state.update { it.copy(isLoading = true, error = null, erroresCampo = emptyMap()) }

        viewModelScope.launch {
            val request = RegistroRequest(actual.nombre.trim(), actual.email.trim(), actual.password)
            val resultado = repository.registrar(urlBase(actual.servidor), request)
            _state.update { aplicar(it, resultado) }
        }
    }

    fun registrarOtro() {
        _state.value = RegistroState(servidor = _state.value.servidor)
    }

    /**
     * "192.168.1.5" -> "http://192.168.1.5:8080/" (IP local, puerto por defecto del backend).
     * Una URL con http:// o https:// (por ejemplo la de la nube) se usa tal cual.
     */
    private fun urlBase(entrada: String): String {
        val texto = entrada.trim().trimEnd('/')
        val conEsquema = texto.startsWith("http://") || texto.startsWith("https://")
        val url = if (conEsquema) texto else "http://$texto"
        val sinPuerto = !conEsquema && ':' !in texto
        return (if (sinPuerto) "$url:8080" else url) + "/"
    }

    private fun aplicar(state: RegistroState, resultado: ResultadoRegistro): RegistroState {
        val base = state.copy(isLoading = false)
        return when (resultado) {
            is ResultadoRegistro.Exito -> base.copy(registroExitoso = resultado.usuario, password = "")
            is ResultadoRegistro.DatosInvalidos -> base.copy(
                erroresCampo = resultado.errores,
                error = if (resultado.errores.isEmpty()) resultado.mensaje else null
            )
            is ResultadoRegistro.EmailDuplicado -> base.copy(error = resultado.mensaje)
            is ResultadoRegistro.ErrorServidor ->
                base.copy(error = "Error del servidor (codigo ${resultado.codigo})")
            ResultadoRegistro.ServidorInvalido ->
                base.copy(error = "La direccion del servidor no es valida. Ejemplo: 192.168.1.5")
            ResultadoRegistro.SinConexion ->
                base.copy(error = "No se pudo conectar con ${state.servidor}. Verifica que el backend este encendido y que la direccion sea la correcta.")
        }
    }
}
