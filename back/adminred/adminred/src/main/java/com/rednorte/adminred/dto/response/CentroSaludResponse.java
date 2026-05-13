package com.rednorte.adminred.dto.response;

public record CentroSaludResponse(Long id, String nombre, String tipo, String region, String comuna, String direccion, Boolean activo, String nivelComplejidad) {
    
}

