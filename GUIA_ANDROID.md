# Guia para completar la parte de Android

## Estado actual
- **Backend:** funciona; los casos 201, 409 y 400 se probaron en Postman.
- **Android:** Retrofit y la pantalla de registro estan implementados, pero **aun no se ha compilado
  ni probado en el emulador** (hacerlo con los pasos de abajo).

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

## Pantalla de registro (ya implementada)
Archivos en `RegistroUsuarios/app/src/main/java/com/ufps/registrousuarios/`:
- `ui/registro/RegistroScreen.kt`: formulario Compose (nombre, correo, contrasena, boton "Crear Cuenta").
- `ui/registro/RegistroViewModel.kt`: `RegistroState` (isLoading, error, erroresCampo, registroExitoso)
  y la llamada dentro de `viewModelScope.launch`.
- `data/RegistroRepository.kt`: llama a `RetrofitClient.authApiService.registrarUsuario(...)` y traduce
  el resultado: 201 (exito), 400 (errores por campo), 409 (correo duplicado), sin conexion.
- `data/remote/ErrorResponse.kt`: JSON de error del backend.
- `MainActivity.kt` muestra `RegistroScreen`.

Flujo para explicar en clase: pantalla -> ViewModel -> Repository -> Retrofit (`POST api/auth/register`) -> backend.

## Probar la conexion
1. Encender PostgreSQL y el backend en **el mismo computador** del emulador (ver [README.md](README.md)).
2. Ejecutar la app en un emulador.
3. Registrar un correo nuevo (esperado: 201, tarjeta con id, nombre y correo).
4. Repetirlo (esperado: 409, mensaje de correo ya registrado).
5. Probar correo invalido o contrasena corta (esperado: 400, error bajo cada campo).
6. Apagar el backend y registrar (esperado: mensaje de "no se pudo conectar").

En un celular fisico `10.0.2.2` no funciona: usar la IP local del PC en `RetrofitClient.kt`
y en `network_security_config.xml`, con ambos equipos en la misma red.
