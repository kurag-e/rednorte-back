package com.rednorte.adminred.service;

import java.util.List;

import com.rednorte.adminred.dto.request.EspecialidadRequest;
import com.rednorte.adminred.dto.response.EspecialidadResponse;

public interface EspecialidadService {

    EspecialidadResponse crear(EspecialidadRequest request);

    List<EspecialidadResponse> listar();
}