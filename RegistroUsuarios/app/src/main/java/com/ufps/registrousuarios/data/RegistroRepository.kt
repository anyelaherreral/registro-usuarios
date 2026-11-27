package com.ufps.registrousuarios.data

import com.google.gson.Gson
import com.ufps.registrousuarios.data.remote.AuthApiService
import com.ufps.registrousuarios.data.remote.ErrorResponse
import com.ufps.registrousuarios.data.remote.RegistroRequest
import com.ufps.registrousuarios.data.remote.RegistroResponse
import com.ufps.registrousuarios.data.remote.RetrofitClient
import java.io.IOException

/** Resultado de intentar registrar, ya traducido desde el codigo HTTP. */
sealed interface ResultadoRegistro {
    data class Exito(val usuario: RegistroResponse) : ResultadoRegistro
    /** HTTP 400: errores por campo (nombre, email, password). */
    data class DatosInvalidos(val mensaje: String, val errores: Map<String, String>) : ResultadoRegistro
    /** HTTP 409: el correo ya existe. */
    data class EmailDuplicado(val mensaje: String) : ResultadoRegistro
    data class ErrorServidor(val codigo: Int) : ResultadoRegistro
    /** El backend esta apagado o no hay red. */
    data object SinConexion : ResultadoRegistro
    /** La direccion del servidor escrita en la app no es valida. */
    data object ServidorInvalido : ResultadoRegistro
}

class RegistroRepository(
    private val apiFor: (String) -> AuthApiService = RetrofitClient::authApiService,
    private val gson: Gson = Gson()
) {

    suspend fun registrar(baseUrl: String, request: RegistroRequest): ResultadoRegistro {
        val api = try {
            apiFor(baseUrl)
        } catch (e: IllegalArgumentException) {
            return ResultadoRegistro.ServidorInvalido
        }
        val response = try {
            api.registrarUsuario(request)
        } catch (e: IOException) {
            return ResultadoRegistro.SinConexion
        }

        val body = response.body()
        if (response.isSuccessful && body != null) return ResultadoRegistro.Exito(body)

        val error = leerError(response.errorBody()?.string())
        return when (response.code()) {
            400 -> ResultadoRegistro.DatosInvalidos(
                mensaje = error?.mensaje ?: "Datos invalidos",
                errores = error?.errores.orEmpty()
            )
            409 -> ResultadoRegistro.EmailDuplicado(error?.mensaje ?: "El correo ya esta registrado")
            else -> ResultadoRegistro.ErrorServidor(response.code())
        }
    }

    private fun leerError(json: String?): ErrorResponse? =
        runCatching { gson.fromJson(json, ErrorResponse::class.java) }.getOrNull()
}
