# Identity Service (Django + DRF + SimpleJWT)

Servicio de identidad/autenticación basado en **Django REST Framework** y **JWT (SimpleJWT)**.

## Features
- Login con **RUT (con dígito verificador)** + contraseña.
- Registro de **pacientes** (público).
- Registro de **doctores** (solo **admin** o **superuser**) y con correo `@rednorte.com`.
- Endpoint `/me` para obtener datos del usuario autenticado.
- Cambio de contraseña (usuario autenticado).

> Nota: La integración con especialidades (Spring Boot) aún no está implementada. En este servicio el campo `especialidad` existe en `Profile` pero no se usa para asignación automática por ahora.

---

## Requisitos
- Python 3.x
- Django
- djangorestframework
- djangorestframework-simplejwt

---

## Base URL
Por defecto (local):

- `http://127.0.0.1:8000/api/v1/`

---

## Cómo ejecutar (local)
Desde la carpeta del proyecto (donde está `manage.py`):

```bash
python manage.py migrate
python manage.py runserver
```

Health check:

- `GET http://127.0.0.1:8000/api/v1/health/`

---

# Endpoints

## 1) Health
### `GET /api/v1/health/`
Respuesta:
```json
{ "status": "ok" }
```

---

## 2) Registro de Paciente (público)
### `POST /api/v1/auth/register/patient/`
Headers:
- `Content-Type: application/json`

Body (ejemplo):
```json
{
  "rut": "12.345.678-5",
  "nombres": "Javi",
  "apellidos": "Perez",
  "genero": "M",
  "mail": "javi@mail.cl",
  "telefono": "987654321",
  "fecha_nacimiento": "2000-01-15",
  "nombre_tutor": "",
  "telefono_tutor": "",
  "password": "ClaveAA12"
}
```

Reglas:
- El RUT se **normaliza** (sin puntos/guión) y se **valida DV** (módulo 11).
- Password fuerte:
  - mínimo 8 caracteres
  - al menos 1 mayúscula
  - al menos 2 números
- No permite RUT o email duplicados.
- Asigna al usuario el rol `patient` (Group).

Respuesta (ejemplo):
```json
{ "message": "Patient registered", "user_id": 10 }
```

---

## 3) Login JWT (obtener tokens)
### `POST /api/v1/auth/token/`
Body:
```json
{
  "username": "123456785",
  "password": "ClaveAA12"
}
```

Notas:
- `username` es el **RUT limpio** (sin puntos ni guión, con DV en mayúscula si es `K`).
- Respuesta contiene `refresh` y `access`.

Respuesta:
```json
{
  "refresh": "....",
  "access": "...."
}
```

---

## 4) Refresh token
### `POST /api/v1/auth/token/refresh/`
Body:
```json
{
  "refresh": "REFRESH_TOKEN_AQUI"
}
```

Respuesta:
```json
{
  "access": "NUEVO_ACCESS_TOKEN"
}
```

---

## 5) Endpoint /me (usuario autenticado)
### `GET /api/v1/me/`
Headers:
- `Authorization: Bearer <ACCESS_TOKEN>`

Respuesta (ejemplo):
```json
{
  "id": 1,
  "rut_login": "123456785",
  "email": "javi@mail.cl",
  "roles": ["patient"],
  "is_staff": false,
  "is_superuser": false,
  "profile": {
    "rut": "123456785",
    "nombres": "Javi",
    "apellidos": "Perez",
    "genero": "M",
    "telefono": "987654321",
    "fecha_nacimiento": "2000-01-15",
    "especialidad": null,
    "nombre_tutor": null,
    "telefono_tutor": null
  }
}
```

---

## 6) Registro de Doctor (solo admin/superuser)
### `POST /api/v1/auth/register/doctor/`
Headers:
- `Authorization: Bearer <ACCESS_TOKEN_ADMIN_O_SUPERUSER>`
- `Content-Type: application/json`

Body (ejemplo):
```json
{
  "rut": "11.111.111-1",
  "nombres": "Ana",
  "apellidos": "Gomez",
  "genero": "F",
  "mail": "ana.gomez@rednorte.com",
  "telefono": "987654321",
  "fecha_nacimiento": "1990-01-01",
  "password": "ClaveAA12"
}
```

Reglas:
- Requiere usuario autenticado con:
  - `is_superuser = true` **o**
  - pertenecer al Group `admin`
- `mail` debe terminar en `@rednorte.com`
- Se valida RUT y password igual que pacientes
- Asigna al usuario el rol `doctor` (Group)

Respuesta:
```json
{ "message": "Doctor registered", "user_id": 6 }
```

---

## 7) Cambiar contraseña (usuario autenticado)
### `POST /api/v1/auth/change-password/`
Headers:
- `Authorization: Bearer <ACCESS_TOKEN>`
- `Content-Type: application/json`

Body:
```json
{
  "old_password": "ClaveAA12",
  "new_password": "ClaveBB34"
}
```

Reglas:
- `old_password` debe ser correcto
- `new_password` debe cumplir la política de password fuerte

Respuesta:
```json
{ "message": "Password updated" }
```

---

# Cómo testear con Postman (paso a paso)

## A) Crear paciente
1. Request: `POST /api/v1/auth/register/patient/`
2. Body JSON (ver ejemplo arriba)
3. Enviar → esperar `201` y `Patient registered`

## B) Login paciente
1. Request: `POST /api/v1/auth/token/`
2. Body:
```json
{ "username": "<RUT_LIMPIO>", "password": "<PASSWORD>" }
```
3. Copiar `access`

## C) Probar /me
1. Request: `GET /api/v1/me/`
2. Authorization:
   - Type: Bearer Token
   - Token: pegar el `access`
3. Enviar → esperar `200`

## D) Login admin/superuser
1. Request: `POST /api/v1/auth/token/`
2. Body con credenciales del admin/superuser
3. Copiar `access`

> Para confirmar que el token es admin/superuser:
> - Llama `GET /api/v1/me/` con ese token y revisa `is_superuser: true` o `roles: ["admin"]`

## E) Registrar doctor (con token admin/superuser)
1. Request: `POST /api/v1/auth/register/doctor/`
2. Authorization: Bearer Token (token del admin/superuser)
3. Body JSON del doctor (mail @rednorte.com)
4. Enviar → esperar `201`

## F) Login doctor y cambiar contraseña
1. Login doctor:
   - `POST /api/v1/auth/token/`
2. Copiar `access` del doctor
3. Cambiar password:
   - `POST /api/v1/auth/change-password/`
   - Authorization: Bearer Token (token del doctor)
   - Body: `{old_password, new_password}`

---

# Errores comunes

## 401 "Token is invalid"
- Asegúrate de usar el campo `access` (no `refresh`)
- No pegar el token con comillas (`"..."`) ni con saltos de línea
- En Postman no escribas `Bearer ` manualmente si estás usando Authorization Type = Bearer Token

## 405 Method Not Allowed
- Revisar el método (ej. `register/doctor` es **POST**, no GET)

## 400 "RUT already exists"
- El usuario ya existe en la base de datos (usa otro RUT o elimina el usuario existente)

---

## Roadmap (pendiente)
- Asignación doctor ↔ pacientes (1 doctor → N pacientes) y transferencia.
- Integración con servicio de especialidades (Spring Boot).