# RedNorte API Gateway

Gateway base compatible con:
- MS Administración de Red (Spring Boot): http://localhost:8081
- MS Gestión Clínica (Spring Boot): http://localhost:8082
- MS Portal Paciente / Auth / Notificaciones (Django): http://localhost:8000

## Ejecutar
```bash
mvn spring-boot:run
```

## Probar
```bash
curl http://localhost:8080/actuator/health
```

Las rutas protegidas requieren:
```http
Authorization: Bearer <jwt>
```

El Gateway valida el JWT y reenvía al microservicio:
- `X-Authenticated-User`
- `X-Authenticated-Role`
