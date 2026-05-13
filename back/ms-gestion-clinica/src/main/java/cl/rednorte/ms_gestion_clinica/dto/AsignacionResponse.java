package cl.rednorte.ms_gestion_clinica.dto;

import java.time.LocalDateTime;

public record AsignacionResponse(
        Long cupoId,
        Long pacienteId,
        String pacienteNombre,
        String especialidad,
        String hospital,
        LocalDateTime fechaHora,
        String estadoNotificacion,
        String mensaje
) {
}
