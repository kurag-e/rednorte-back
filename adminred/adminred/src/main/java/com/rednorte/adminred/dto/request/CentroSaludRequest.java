package com.rednorte.adminred.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CentroSaludRequest(@NotBlank @Size(max=150) String nombre,@NotBlank @Pattern(regexp="HOSPITAL|CAPS|CLINICA", message="tipo debe ser HOSPITAL, CAPS o CLINICA") String tipo,@NotBlank @Size(max=100) String region,@NotBlank @Size(max=100) String comuna,@NotBlank @Size(max=200) String direccion,@Size(max=50) String nivelComplejidad) {

}
