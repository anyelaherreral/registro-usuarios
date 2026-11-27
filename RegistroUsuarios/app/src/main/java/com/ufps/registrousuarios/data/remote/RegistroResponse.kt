package com.ufps.registrousuarios.data.remote

data class RegistroResponse(
    val id: Long,
    val nombre: String,
    val email: String,
    val mensaje: String
)
