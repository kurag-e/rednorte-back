package com.rednorte.adminred.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rednorte.adminred.dto.request.EspecialidadRequest;
import com.rednorte.adminred.dto.response.EspecialidadResponse;
import com.rednorte.adminred.service.EspecialidadService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/especialidades")
public class EspecialidadController {
    private final EspecialidadService service;

    public EspecialidadController(EspecialidadService service){
        this.service = service; 
    }

    @PostMapping 
    public ResponseEntity<EspecialidadResponse> crear(@Valid @RequestBody EspecialidadRequest request){ 
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request)); 
    }

    @GetMapping 
    public ResponseEntity<List<EspecialidadResponse>> listar(){ 
        return ResponseEntity.ok(service.listar()); 
    }
}