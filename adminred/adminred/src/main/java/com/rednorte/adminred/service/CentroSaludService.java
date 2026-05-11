package com.rednorte.adminred.service;

import java.util.List;

import com.rednorte.adminred.dto.request.CentroEspecialidadRequest;
import com.rednorte.adminred.dto.request.CentroSaludRequest;
import com.rednorte.adminred.dto.request.EstadoCentroRequest;
import com.rednorte.adminred.dto.response.CentroEspecialidadResponse;
import com.rednorte.adminred.dto.response.CentroSaludResponse;

public interface CentroSaludService {
    CentroSaludResponse crear(CentroSaludRequest request); 
    List<CentroSaludResponse> listar(String region,String comuna,String tipo,Boolean soloActivos); 
    CentroSaludResponse buscarPorId(Long id); 
    CentroSaludResponse actualizar(Long id, CentroSaludRequest request); 
    CentroSaludResponse cambiarEstado(Long id, EstadoCentroRequest request); 
    CentroEspecialidadResponse agregarEspecialidad(Long centroId, CentroEspecialidadRequest request); 
    List<CentroEspecialidadResponse> listarEspecialidades(Long centroId);
}
