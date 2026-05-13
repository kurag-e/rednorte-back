package com.rednorte.adminred.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rednorte.adminred.model.Especialidad;

public interface EspecialidadRepository extends JpaRepository<Especialidad,Long>{ 
    Optional<Especialidad> findByNombreIgnoreCase(String nombre); 
}

