package com.rednorte.adminred.mapper;

import com.rednorte.adminred.dto.request.CentroSaludRequest;
import com.rednorte.adminred.dto.response.CentroSaludResponse;
import com.rednorte.adminred.model.CentroSalud;

public class CentroSaludMapper {
    public static CentroSalud toEntity(CentroSaludRequest r){ 
        return CentroSalud.builder().nombre(r.nombre()).tipo(r.tipo()).region(r.region()).comuna(r.comuna()).direccion(r.direccion()).nivelComplejidad(r.nivelComplejidad()).activo(true).build(); 
    }

    public static CentroSaludResponse toResponse(CentroSalud c){ 
        return new CentroSaludResponse(c.getId(),c.getNombre(),c.getTipo(),c.getRegion(),c.getComuna(),c.getDireccion(),c.getActivo(),c.getNivelComplejidad()); 
    }

    public static void update(CentroSalud c, CentroSaludRequest r){ 
        c.setNombre(r.nombre()); c.setTipo(r.tipo()); c.setRegion(r.region()); c.setComuna(r.comuna()); c.setDireccion(r.direccion()); c.setNivelComplejidad(r.nivelComplejidad()); 
    }
}
