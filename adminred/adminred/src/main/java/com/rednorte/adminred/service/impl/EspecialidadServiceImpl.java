package com.rednorte.adminred.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rednorte.adminred.dto.request.EspecialidadRequest;
import com.rednorte.adminred.dto.response.EspecialidadResponse;
import com.rednorte.adminred.mapper.EspecialidadMapper;
import com.rednorte.adminred.model.Especialidad;
import com.rednorte.adminred.repository.EspecialidadRepository;
import com.rednorte.adminred.service.EspecialidadService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor

public class EspecialidadServiceImpl implements EspecialidadService{
    private final EspecialidadRepository repository;

    public EspecialidadResponse crear(EspecialidadRequest request){ 
        Especialidad e=repository.findByNombreIgnoreCase(request.nombre()).orElseGet(()->repository.save(Especialidad.builder().nombre(request.nombre()).build()));
        return EspecialidadMapper.toResponse(e); 
    }

    public List<EspecialidadResponse> listar(){ 
        return repository.findAll().stream().map(EspecialidadMapper::toResponse).toList(); 
    }
}

