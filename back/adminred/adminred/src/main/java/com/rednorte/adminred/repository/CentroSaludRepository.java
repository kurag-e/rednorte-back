package com.rednorte.adminred.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rednorte.adminred.model.CentroSalud;

public interface CentroSaludRepository extends JpaRepository<CentroSalud, Long> {
    List<CentroSalud> findByActivoTrue();
    List<CentroSalud> findByRegionIgnoreCase(String region);
    List<CentroSalud> findByComunaIgnoreCase(String comuna);
    List<CentroSalud> findByTipoIgnoreCase(String tipo);
    List<CentroSalud> findByRegionIgnoreCaseAndComunaIgnoreCase(String region, String comuna);
}