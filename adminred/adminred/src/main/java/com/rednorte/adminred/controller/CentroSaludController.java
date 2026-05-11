package com.rednorte.adminred.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rednorte.adminred.dto.request.CentroEspecialidadRequest;
import com.rednorte.adminred.dto.request.CentroSaludRequest;
import com.rednorte.adminred.dto.request.EstadoCentroRequest;
import com.rednorte.adminred.dto.response.CentroEspecialidadResponse;
import com.rednorte.adminred.dto.response.CentroSaludResponse;
import com.rednorte.adminred.service.CentroSaludService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController @RequestMapping("/api/centros") @RequiredArgsConstructor
public class CentroSaludController {
    private final CentroSaludService service;
    
    @PostMapping 
    public ResponseEntity<CentroSaludResponse> crear(@Valid @RequestBody CentroSaludRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request)); 
    }

    @GetMapping 
    public ResponseEntity<List<CentroSaludResponse>> listar(@RequestParam(required=false) String region,@RequestParam(required=false) String comuna,@RequestParam(required=false) String tipo,@RequestParam(required=false, defaultValue="false") Boolean soloActivos){ 
        return ResponseEntity.ok(service.listar(region,comuna,tipo,soloActivos)); 
    }
 
    @GetMapping("/{id}") 
    public ResponseEntity<CentroSaludResponse> buscar(@PathVariable Long id){ 
        return ResponseEntity.ok(service.buscarPorId(id)); 
    }
 
    @PutMapping("/{id}") 
    public ResponseEntity<CentroSaludResponse> actualizar(@PathVariable Long id,@Valid @RequestBody CentroSaludRequest request){ 
        return ResponseEntity.ok(service.actualizar(id,request)); 
    }
 
    @PatchMapping("/{id}/estado") 
    public ResponseEntity<CentroSaludResponse> cambiarEstado(@PathVariable Long id,@Valid @RequestBody EstadoCentroRequest request){ 
        return ResponseEntity.ok(service.cambiarEstado(id,request)); 
    }
 
    @PostMapping("/{id}/especialidades") 
    public ResponseEntity<CentroEspecialidadResponse> agregarEspecialidad(@PathVariable Long id,@Valid @RequestBody CentroEspecialidadRequest request){ 
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregarEspecialidad(id,request)); 
    }
 
    @GetMapping("/{id}/especialidades") 
    public ResponseEntity<List<CentroEspecialidadResponse>> especialidades(@PathVariable Long id){ 
        return ResponseEntity.ok(service.listarEspecialidades(id)); 
    }
}
