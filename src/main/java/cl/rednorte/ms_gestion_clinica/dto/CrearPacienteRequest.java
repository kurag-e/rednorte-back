package cl.rednorte.ms_gestion_clinica.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearPacienteRequest(
        @NotBlank @Size(max = 20) String rut,
        @NotBlank @Size(max = 120) String nombre,
        @NotBlank @Size(max = 80) String hospitalReferencia,
        @NotBlank @Size(max = 80) String especialidad,
        @Min(1) @Max(5) int nivelUrgencia
) {
}
