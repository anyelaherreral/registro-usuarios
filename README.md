# Registro de usuarios: Spring Boot + Android (Retrofit)

| Carpeta | Contenido | Programa |
|---|---|---|
| `backend/` | API REST en Spring Boot 4.1.1 con PostgreSQL | VS Code |
| `RegistroUsuarios/` | Proyecto Android en Kotlin con la configuracion de Retrofit | Android Studio |
| `database/` | Script para crear la base de datos | pgAdmin |

## Ejecutar el backend
Requisitos: JDK 17 o superior (probado con 19), PostgreSQL (probado con 16.4) y la extension
**Extension Pack for Java** de VS Code.

1. Ejecutar `database/crear_base.sql` en pgAdmin.
2. Abrir la carpeta `backend` en VS Code y confiar en la carpeta (sin "Modo restringido").
3. Poner la clave local de PostgreSQL en `src/main/resources/application.properties`,
   en `${DB_PASSWORD:postgres}` (reemplazar solo `postgres`). **No subir ese cambio.**
4. Esperar la importacion del proyecto y usar **Run Java** en `RegistroApplication.java`
   (no usar "Run Code").
5. Esperar el mensaje `Started RegistroApplication`.

## Endpoint
`POST http://localhost:8080/api/auth/register`

```json
{ "nombre": "Carlos Pérez", "email": "carlos@correo.com", "password": "MiPassword123" }
```

| Caso | Codigo | Probado en Postman |
|---|---|---|
| Registro correcto | 201 | Si |
| Correo repetido | 409 | Si |
| Datos invalidos | 400 | Si |
| JSON mal escrito | 400 | Si |

Tambien se verifico en PostgreSQL que la contrasena se guarda como hash BCrypt.

## Android
Ver [GUIA_ANDROID.md](GUIA_ANDROID.md).
