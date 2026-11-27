# Guia para completar la parte de Android

## Estado actual
- **Backend:** funciona; los casos 201, 409 y 400 se probaron en Postman.
- **Android:** la configuracion de Retrofit esta agregada, pero **no se ha confirmado que compile**
  ni se ha probado la conexion desde el emulador.

Ya esta hecho en `RegistroUsuarios/`:
- Dependencias de Retrofit, Gson y corrutinas en `app/build.gradle.kts`.
- `RegistroRequest`, `RegistroResponse`, `AuthApiService` y `RetrofitClient`
  en el paquete `com.ufps.registrousuarios.data.remote`.
- URL base `http://10.0.2.2:8080/` (el PC visto desde el emulador).
- Permiso `INTERNET` y `network_security_config.xml` (HTTP solo hacia `10.0.2.2`).

## Pasos
1. Clonar el repositorio: `git clone <URL-del-repositorio>`
2. En Android Studio, **Open** y elegir la carpeta `RegistroUsuarios` (no la raiz del repositorio).
   `local.properties` se genera en cada computador y no se sube.
3. El proyecto usa Android Gradle Plugin 9.4.1 y `compileSdk 37`. Si Android Studio lo pide,
   actualizarlo o instalar ese SDK.
4. **File > Sync Project with Gradle Files**.
5. **Build > Assemble Project**. Resultado esperado: `BUILD SUCCESSFUL`.
   Si falla, revisar el primer error de la pestana **Build**.

## Que pide la actividad en Android
Las indicaciones piden el `data class RegistroRequest`, la interfaz `AuthApiService`,
la URL base del emulador y el permiso de red. Esos archivos ya estan; falta compilarlos y
mostrar la evidencia (captura de `BUILD SUCCESSFUL`, los archivos Kotlin y el manifest).

## Registro desde la app (confirmar con el profesor si lo exige)
La app todavia muestra la pantalla de ejemplo: **no hay formulario y no envia datos**.
Las diapositivas de clase tambien muestran lo siguiente:
1. `RegistroState` (isLoading, error, registroExitoso).
2. `RegistroViewModel`: llama a `RetrofitClient.authApiService.registrarUsuario(...)`
   dentro de `viewModelScope.launch`, con `try/catch` para fallos de conexion.
   Mostrar mensajes distintos para 400 y 409.
3. `RegistroScreen` en Compose: campos nombre, correo y contrasena, y boton "Crear Cuenta".
4. Mostrar esa pantalla desde `MainActivity`.
5. Si se usa `viewModel()` en Compose, agregar la dependencia
   `androidx.lifecycle:lifecycle-viewmodel-compose`.

Las diapositivas tambien mencionan Clean Architecture (capas Domain, Data y Presentacion).
Confirmar si el profesor la exige.

## Probar la conexion
Necesita la pantalla anterior (u otro codigo que llame al servicio).
1. Encender PostgreSQL y el backend en **el mismo computador** del emulador (ver [README.md](README.md)).
2. Ejecutar la app en un emulador.
3. Registrar un correo nuevo (esperado: 201) y repetirlo (esperado: 409).

En un celular fisico `10.0.2.2` no funciona: usar la IP local del PC en `RetrofitClient.kt`
y en `network_security_config.xml`, con ambos equipos en la misma red.
