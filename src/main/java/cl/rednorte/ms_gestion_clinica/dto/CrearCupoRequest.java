package cl.rednorte.ms_gestion_clinica.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CrearCupoRequest(
        @NotBlank @Size(max = 80) String hospital,
        @NotBlank @Size(max = 80) String especialidad,
        @NotNull @Future LocalDateTime fechaHora
) {
}
