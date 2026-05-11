package cl.rednorte.ms_gestion_clinica.repository;

import cl.rednorte.ms_gestion_clinica.model.EstadoListaEspera;
import cl.rednorte.ms_gestion_clinica.model.PacienteListaEspera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PacienteListaEsperaRepository extends JpaRepository<PacienteListaEspera, Long> {

    Optional<PacienteListaEspera> findByRut(String rut);

    List<PacienteListaEspera> findByEspecialidadAndEstadoOrderByPuntajePrioridadDescFechaRegistroAsc(
            String especialidad,
            EstadoListaEspera estado
    );
}
