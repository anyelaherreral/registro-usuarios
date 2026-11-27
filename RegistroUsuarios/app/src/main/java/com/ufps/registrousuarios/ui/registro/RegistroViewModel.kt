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
            val resultado = repository.registrar(request)
            _state.update { aplicar(it, resultado) }
        }
    }

    fun registrarOtro() {
        _state.value = RegistroState()
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
            ResultadoRegistro.SinConexion ->
                base.copy(error = "No se pudo conectar con el servidor. Verifica que el backend este encendido.")
        }
    }
}
