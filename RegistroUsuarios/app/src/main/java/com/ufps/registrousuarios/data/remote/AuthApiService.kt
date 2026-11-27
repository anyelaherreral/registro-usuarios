package com.ufps.registrousuarios.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/register")
    suspend fun registrarUsuario(@Body request: RegistroRequest): Response<RegistroResponse>
}
