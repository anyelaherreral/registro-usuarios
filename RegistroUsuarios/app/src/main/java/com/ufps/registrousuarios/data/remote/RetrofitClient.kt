package com.ufps.registrousuarios.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 10.0.2.2 = localhost del PC visto desde el emulador de Android
    const val DEFAULT_BASE_URL = "http://10.0.2.2:8080/"

    private var cachedUrl: String? = null
    private var cachedService: AuthApiService? = null

    /** Crea (o reutiliza) el servicio para la URL dada. Lanza IllegalArgumentException si la URL no es valida. */
    @Synchronized
    fun authApiService(baseUrl: String = DEFAULT_BASE_URL): AuthApiService {
        val cached = cachedService
        if (cached != null && cachedUrl == baseUrl) return cached
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
            .also { cachedUrl = baseUrl; cachedService = it }
    }
}
