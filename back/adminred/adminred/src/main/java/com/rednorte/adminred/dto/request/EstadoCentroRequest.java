package com.rednorte.adminred.dto.request;

import jakarta.validation.constraints.NotNull;

public record EstadoCentroRequest(@NotNull Boolean activo) {

}
