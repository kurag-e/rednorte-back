package com.rednorte.adminred.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rednorte.adminred.dto.request.CentroEspecialidadRequest;
import com.rednorte.adminred.dto.request.CentroSaludRequest;
import com.rednorte.adminred.dto.request.EstadoCentroRequest;
import com.rednorte.adminred.dto.response.CentroEspecialidadResponse;
import com.rednorte.adminred.dto.response.CentroSaludResponse;
import com.rednorte.adminred.exception.BusinessException;
import com.rednorte.adminred.exception.ResourceNotFoundException;
import com.rednorte.adminred.mapper.CentroSaludMapper;
import com.rednorte.adminred.mapper.EspecialidadMapper;
import com.rednorte.adminred.model.CentroEspecialidad;
import com.rednorte.adminred.model.CentroSalud;
import com.rednorte.adminred.model.Especialidad;
import com.rednorte.adminred.repository.CentroEspecialidadRepository;
import com.rednorte.adminred.repository.CentroSaludRepository;
import com.rednorte.adminred.repository.EspecialidadRepository;
import com.rednorte.adminred.service.CentroSaludService;

import lombok.RequiredArgsConstructor;


@Service 
@RequiredArgsConstructor
public class CentroSaludServiceImpl implements CentroSaludService{
    private final CentroSaludRepository centroRepository; 
    private final EspecialidadRepository especialidadRepository; 
    private final CentroEspecialidadRepository centroEspecialidadRepository;
 
    @Transactional public CentroSaludResponse crear(CentroSaludRequest request){ 
        CentroSalud c=CentroSaludMapper.toEntity(request); 
        return CentroSaludMapper.toResponse(centroRepository.save(c)); 
    }

public List<CentroSaludResponse> listar(String region,String comuna,String tipo,Boolean soloActivos){ 
    List<CentroSalud> centros; 

    if(Boolean.TRUE.equals(soloActivos)) 
        centros = centroRepository.findByActivoTrue(); 

    else if(region != null && comuna != null) 
        centros = centroRepository.findByRegionIgnoreCaseAndComunaIgnoreCase(region, comuna); 

    else if(region != null) 
        centros = centroRepository.findByRegionIgnoreCase(region); 

    else if(comuna != null) 
        centros = centroRepository.findByComunaIgnoreCase(comuna); 

    else if(tipo != null) 
        centros = centroRepository.findByTipoIgnoreCase(tipo); 

    else 
        centros = centroRepository.findAll(); 

    return centros.stream().map(CentroSaludMapper::toResponse).toList(); 
}

    @Transactional 
    public CentroSaludResponse actualizar(Long id, CentroSaludRequest request){ 
        CentroSalud c=obtenerCentro(id); 
        CentroSaludMapper.update(c,request); 
        return CentroSaludMapper.toResponse(centroRepository.save(c)); 
    }

    @Transactional 
    public CentroSaludResponse cambiarEstado(Long id, EstadoCentroRequest request){ 
        CentroSalud c=obtenerCentro(id); 
        c.setActivo(request.activo()); 
        return CentroSaludMapper.toResponse(centroRepository.save(c)); 
    }

    @Transactional 
    public CentroEspecialidadResponse agregarEspecialidad(Long centroId, CentroEspecialidadRequest request){ 
        CentroSalud c=obtenerCentro(centroId); 
        Especialidad e=especialidadRepository.findById(request.especialidadId()).orElseThrow(()->new ResourceNotFoundException("Especialidad no encontrada")); 
        if(centroEspecialidadRepository.existsByCentroIdAndEspecialidadId(centroId, request.especialidadId())) throw new BusinessException("La especialidad ya esta asociada al centro"); 
        CentroEspecialidad ce=CentroEspecialidad.builder().centro(c).especialidad(e).capacidadDiaria(request.capacidadDiaria()).build(); 
        return EspecialidadMapper.toResponse(centroEspecialidadRepository.save(ce)); 
    }

    public List<CentroEspecialidadResponse> listarEspecialidades(Long centroId){
        obtenerCentro(centroId); 
        return centroEspecialidadRepository.findByCentroId(centroId).stream().map(EspecialidadMapper::toResponse).toList(); 
        }

    private CentroSalud obtenerCentro(Long id){ 
        return centroRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Centro de salud no encontrado")); 
    }

    @Override
    public CentroSaludResponse buscarPorId(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buscarPorId'");
    }
}
