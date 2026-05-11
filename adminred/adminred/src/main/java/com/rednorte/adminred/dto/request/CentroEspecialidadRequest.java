package com.rednorte.adminred.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CentroEspecialidadRequest(@NotNull Long especialidadId, @NotNull @Min(1) Integer capacidadDiaria) {

}
