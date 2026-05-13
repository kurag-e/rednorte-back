# Microservicio de Gestion Clinica - RedNorte

Microservicio transaccional en Java 21 (Spring Boot) para administrar listas de espera hospitalarias con prioridad por urgencia, reasignacion automatica de cupos y resiliencia ante fallos de hospitales perifericos.

## Capacidades implementadas

- Gestion de lista de espera con prioridad basada en urgencia medica.
- Reasignacion automatica cuando se libera un cupo de agenda.
- Resiliencia con circuit breaker (`hospitalPeriferico`) para mantener el nucleo operativo.
- Persistencia en MySQL compatible con XAMPP/phpMyAdmin.

## Requisitos

- Java 21
- Maven 3.9+
- MySQL (XAMPP) activo

## Configuracion de base de datos (XAMPP/phpMyAdmin)

1. Iniciar `Apache` y `MySQL` en XAMPP.
2. Crear la BD ejecutando `database/init.sql` en phpMyAdmin.
3. Ajustar credenciales por variables de entorno si aplica:
   - `DB_URL`
   - `DB_USER`
   - `DB_PASSWORD`

Valores por defecto:

- URL: `jdbc:mysql://localhost:3306/rednorte_clinica`
- Usuario: `root`
- Password: vacio

## Ejecutar en VS Code

```bash
./mvnw spring-boot:run
```

En Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

## Endpoints principales

Base: `http://localhost:8082/api/clinica`

- `POST /lista-espera` registrar paciente en espera.
- `GET /lista-espera` listar pacientes.
- `POST /agenda/cupos` crear cupo disponible.
- `GET /agenda/cupos` listar cupos.
- `POST /agenda/cupos/{cupoId}/reasignar` asignar cupo a siguiente paciente elegible.
- `POST /agenda/cupos/{cupoId}/cancelacion` liberar cupo y reasignar automaticamente.

## Ejemplos JSON

Registrar paciente:

```json
{
  "rut": "11.111.111-1",
  "nombre": "Ana Soto",
  "hospitalReferencia": "Hospital de Los Andes",
  "especialidad": "Cardiologia",
  "nivelUrgencia": 5
}
```

Crear cupo:

```json
{
  "hospital": "Hospital de Los Andes",
  "especialidad": "Cardiologia",
  "fechaHora": "2026-05-02T10:30:00"
}
```

## Verificacion rapida

```powershell
.\mvnw.cmd -DskipTests package
.\mvnw.cmd test
```
