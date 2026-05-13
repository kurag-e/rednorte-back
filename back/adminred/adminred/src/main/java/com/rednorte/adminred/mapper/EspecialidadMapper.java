package com.rednorte.adminred.mapper;

import com.rednorte.adminred.dto.response.CentroEspecialidadResponse;
import com.rednorte.adminred.dto.response.EspecialidadResponse;
import com.rednorte.adminred.model.CentroEspecialidad;
import com.rednorte.adminred.model.Especialidad;

public class EspecialidadMapper {
    public static EspecialidadResponse toResponse(Especialidad e){ 
        return new EspecialidadResponse(e.getId(), e.getNombre()); 
    }

    public static CentroEspecialidadResponse toResponse(CentroEspecialidad ce){ 
        return new CentroEspecialidadResponse(ce.getId(), ce.getCentro().getId(), ce.getCentro().getNombre(), ce.getEspecialidad().getId(), ce.getEspecialidad().getNombre(), ce.getCapacidadDiaria()); 
    }
}
