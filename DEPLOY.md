# Publicar el backend en Render (gratis)

Asi el backend queda con una URL publica (`https://...onrender.com`) y la app funciona desde cualquier celular,
sin depender de la IP del PC.

1. Entrar a https://render.com e iniciar sesion con GitHub (la cuenta que tiene este repositorio).
2. **New > Blueprint** y elegir este repositorio. Render lee `render.yaml` y crea:
   - `registro-backend` (Spring Boot, se construye con `backend/Dockerfile`)
   - `registro-db` (PostgreSQL)
   Las variables `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER` y `DB_PASSWORD` se conectan solas.
3. Esperar a que el servicio diga **Live** (la primera vez tarda varios minutos).
4. Probar en el navegador del celular: `https://TU-SERVICIO.onrender.com/api/auth/register`
   debe responder `405` (el endpoint solo acepta POST), eso significa que esta arriba.
5. En la app, en el campo **Servidor**, pegar la URL completa `https://TU-SERVICIO.onrender.com`.

Limitaciones del plan gratis (importante para la exposicion):
- El servicio se **duerme tras 15 minutos** sin uso; la primera peticion despues tarda ~1 minuto.
  Antes de exponer, abrir la URL del paso 4 para despertarlo.
- La base de datos gratis de Render **expira a los 30 dias**.
- La tabla `usuarios` la crea Hibernate al arrancar (`ddl-auto=update`); no hay que ejecutar `crear_base.sql`.
