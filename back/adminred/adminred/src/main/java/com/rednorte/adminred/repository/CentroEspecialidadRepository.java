package com.rednorte.adminred.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rednorte.adminred.model.CentroEspecialidad;

public interface CentroEspecialidadRepository extends JpaRepository<CentroEspecialidad,Long>{
    List<CentroEspecialidad> findByCentroId(Long centroId);
    boolean existsByCentroIdAndEspecialidadId(Long centroId, Long especialidadId);
}