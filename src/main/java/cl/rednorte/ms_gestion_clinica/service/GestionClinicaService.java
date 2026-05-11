package cl.rednorte.ms_gestion_clinica.service;

import cl.rednorte.ms_gestion_clinica.dto.AsignacionResponse;
import cl.rednorte.ms_gestion_clinica.dto.CrearCupoRequest;
import cl.rednorte.ms_gestion_clinica.dto.CrearPacienteRequest;
import cl.rednorte.ms_gestion_clinica.model.CupoAgenda;
import cl.rednorte.ms_gestion_clinica.model.EstadoCupoAgenda;
import cl.rednorte.ms_gestion_clinica.model.EstadoListaEspera;
import cl.rednorte.ms_gestion_clinica.model.EstadoNotificacion;
import cl.rednorte.ms_gestion_clinica.model.PacienteListaEspera;
import cl.rednorte.ms_gestion_clinica.repository.CupoAgendaRepository;
import cl.rednorte.ms_gestion_clinica.repository.PacienteListaEsperaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GestionClinicaService {

    private final PacienteListaEsperaRepository pacienteRepository;
    private final CupoAgendaRepository cupoRepository;
    private final HospitalPerifericoClient hospitalPerifericoClient;

    public GestionClinicaService(
            PacienteListaEsperaRepository pacienteRepository,
            CupoAgendaRepository cupoRepository,
            HospitalPerifericoClient hospitalPerifericoClient
    ) {
        this.pacienteRepository = pacienteRepository;
        this.cupoRepository = cupoRepository;
        this.hospitalPerifericoClient = hospitalPerifericoClient;
    }

    @Transactional
    public PacienteListaEspera registrarPaciente(CrearPacienteRequest request) {
        pacienteRepository.findByRut(request.rut()).ifPresent(existing -> {
            throw new IllegalArgumentException("Ya existe un paciente en lista de espera con el RUT indicado");
        });

        PacienteListaEspera paciente = new PacienteListaEspera();
        paciente.setRut(request.rut());
        paciente.setNombre(request.nombre());
        paciente.setHospitalReferencia(request.hospitalReferencia());
        paciente.setEspecialidad(request.especialidad());
        paciente.setNivelUrgencia(request.nivelUrgencia());
        paciente.setPuntajePrioridad(calcularPuntajePrioridad(request.nivelUrgencia()));
        paciente.setEstado(EstadoListaEspera.EN_ESPERA);
        return pacienteRepository.save(paciente);
    }

    @Transactional
    public CupoAgenda crearCupo(CrearCupoRequest request) {
        CupoAgenda cupo = new CupoAgenda();
        cupo.setHospital(request.hospital());
        cupo.setEspecialidad(request.especialidad());
        cupo.setFechaHora(request.fechaHora());
        cupo.setEstado(EstadoCupoAgenda.DISPONIBLE);
        cupo.setEstadoNotificacion(EstadoNotificacion.PENDIENTE);
        return cupoRepository.save(cupo);
    }

    @Transactional
    public AsignacionResponse registrarCancelacionYReasignar(Long cupoId) {
        CupoAgenda cupo = cupoRepository.findById(cupoId)
                .orElseThrow(() -> new EntityNotFoundException("Cupo de agenda no encontrado"));

        cupo.setEstado(EstadoCupoAgenda.DISPONIBLE);
        cupo.setPacienteId(null);
        cupo.setEstadoNotificacion(EstadoNotificacion.PENDIENTE);
        cupoRepository.save(cupo);

        return reasignarCupoDisponible(cupoId);
    }

    @Transactional
    public AsignacionResponse reasignarCupoDisponible(Long cupoId) {
        CupoAgenda cupo = cupoRepository.findById(cupoId)
                .orElseThrow(() -> new EntityNotFoundException("Cupo de agenda no encontrado"));

        if (cupo.getEstado() != EstadoCupoAgenda.DISPONIBLE) {
            return new AsignacionResponse(
                    cupo.getId(),
                    null,
                    null,
                    cupo.getEspecialidad(),
                    cupo.getHospital(),
                    cupo.getFechaHora(),
                    cupo.getEstadoNotificacion().name(),
                    "El cupo no esta disponible para reasignacion"
            );
        }

        List<PacienteListaEspera> candidatos = pacienteRepository
                .findByEspecialidadAndEstadoOrderByPuntajePrioridadDescFechaRegistroAsc(
                        cupo.getEspecialidad(),
                        EstadoListaEspera.EN_ESPERA
                );

        Optional<PacienteListaEspera> seleccionado = candidatos.stream().findFirst();
        if (seleccionado.isEmpty()) {
            return new AsignacionResponse(
                    cupo.getId(),
                    null,
                    null,
                    cupo.getEspecialidad(),
                    cupo.getHospital(),
                    cupo.getFechaHora(),
                    cupo.getEstadoNotificacion().name(),
                    "No hay pacientes elegibles en lista de espera"
            );
        }

        PacienteListaEspera paciente = seleccionado.get();
        paciente.setEstado(EstadoListaEspera.ASIGNADO);
        paciente.setFechaAsignacion(LocalDateTime.now());
        paciente.setCupoAgendaId(cupo.getId());
        pacienteRepository.save(paciente);

        cupo.setPacienteId(paciente.getId());
        cupo.setEstado(EstadoCupoAgenda.RESERVADO);

        boolean notificacionExitosa = hospitalPerifericoClient.notificarAsignacion(
                cupo.getId(),
                paciente.getId(),
                cupo.getHospital()
        );

        cupo.setEstadoNotificacion(notificacionExitosa ? EstadoNotificacion.CONFIRMADA : EstadoNotificacion.PENDIENTE);
        cupoRepository.save(cupo);

        String mensaje = notificacionExitosa
                ? "Cupo reasignado y agenda sincronizada con hospital periferico"
                : "Cupo reasignado. Notificacion pendiente por falla temporal del hospital periferico";

        return new AsignacionResponse(
                cupo.getId(),
                paciente.getId(),
                paciente.getNombre(),
                cupo.getEspecialidad(),
                cupo.getHospital(),
                cupo.getFechaHora(),
                cupo.getEstadoNotificacion().name(),
                mensaje
        );
    }

    @Transactional(readOnly = true)
    public List<PacienteListaEspera> listarPacientesEnEspera() {
        return pacienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CupoAgenda> listarCupos() {
        return cupoRepository.findAll();
    }

    int calcularPuntajePrioridad(int nivelUrgencia) {
        return switch (nivelUrgencia) {
            case 5 -> 500;
            case 4 -> 400;
            case 3 -> 300;
            case 2 -> 200;
            case 1 -> 100;
            default -> throw new IllegalArgumentException("Nivel de urgencia invalido");
        };
    }
}
