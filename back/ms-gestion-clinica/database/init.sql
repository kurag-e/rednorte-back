CREATE DATABASE IF NOT EXISTS rednorte_clinica;
USE rednorte_clinica;

CREATE TABLE IF NOT EXISTS pacientes_lista_espera (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rut VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(120) NOT NULL,
    hospital_referencia VARCHAR(80) NOT NULL,
    especialidad VARCHAR(80) NOT NULL,
    nivel_urgencia INT NOT NULL,
    puntaje_prioridad INT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_registro DATETIME NOT NULL,
    fecha_asignacion DATETIME NULL,
    cupo_agenda_id BIGINT NULL
);

CREATE TABLE IF NOT EXISTS cupos_agenda (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    hospital VARCHAR(80) NOT NULL,
    especialidad VARCHAR(80) NOT NULL,
    fecha_hora DATETIME NOT NULL,
    estado VARCHAR(20) NOT NULL,
    paciente_id BIGINT NULL,
    estado_notificacion VARCHAR(20) NOT NULL
);
