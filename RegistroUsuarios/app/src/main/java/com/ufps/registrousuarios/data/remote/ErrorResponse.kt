package com.ufps.registrousuarios.data.remote

/** Error JSON que devuelve el backend en 400 y 409. */
data class ErrorResponse(
    val status: Int,
    val mensaje: String,
    val errores: Map<String, String>? = null
)
