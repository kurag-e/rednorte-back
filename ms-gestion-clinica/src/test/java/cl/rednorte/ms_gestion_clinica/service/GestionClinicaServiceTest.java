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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GestionClinicaServiceTest {

        @Mock
        private PacienteListaEsperaRepository pacienteRepository;

        @Mock
        private CupoAgendaRepository cupoRepository;

        @Mock
        private HospitalPerifericoClient hospitalPerifericoClient;

        @InjectMocks
    private GestionClinicaService gestionClinicaService;

    @Test
    void reasignarDebeSeleccionarMayorUrgencia() {
        when(hospitalPerifericoClient.notificarAsignacion(anyLong(), anyLong(), anyString())).thenReturn(true);

                List<PacienteListaEspera> pacientes = new ArrayList<>();
                List<CupoAgenda> cupos = new ArrayList<>();

                when(pacienteRepository.findByRut(anyString()))
                                .thenAnswer(invocation -> pacientes.stream()
                                                .filter(p -> p.getRut().equals(invocation.getArgument(0)))
                                                .findFirst());

                when(pacienteRepository.save(any(PacienteListaEspera.class))).thenAnswer(invocation -> {
                        PacienteListaEspera paciente = invocation.getArgument(0);
                        if (paciente.getId() == null) {
                                paciente.setId((long) (pacientes.size() + 1));
                                pacientes.add(paciente);
                        }
                        return paciente;
                });

                when(pacienteRepository.findByEspecialidadAndEstadoOrderByPuntajePrioridadDescFechaRegistroAsc(anyString(), any()))
                                .thenAnswer(invocation -> pacientes.stream()
                                                .filter(p -> invocation.getArgument(0).equals(p.getEspecialidad()))
                                                .filter(p -> invocation.getArgument(1) == p.getEstado())
                                                .sorted((a, b) -> Integer.compare(b.getPuntajePrioridad(), a.getPuntajePrioridad()))
                                                .toList());

                when(pacienteRepository.findAll()).thenReturn(pacientes);

                when(cupoRepository.save(any(CupoAgenda.class))).thenAnswer(invocation -> {
                        CupoAgenda cupo = invocation.getArgument(0);
                        if (cupo.getId() == null) {
                                cupo.setId((long) (cupos.size() + 1));
                                cupos.add(cupo);
                        }
                        return cupo;
                });

                when(cupoRepository.findById(anyLong()))
                                .thenAnswer(invocation -> cupos.stream()
                                                .filter(c -> c.getId().equals(invocation.getArgument(0)))
                                                .findFirst());

                when(cupoRepository.findAll()).thenReturn(cupos);

        PacienteListaEspera pacienteUrgenciaBaja = gestionClinicaService.registrarPaciente(
                new CrearPacienteRequest("1-9", "Paciente Baja", "Hospital A", "Cardiologia", 2)
        );

        PacienteListaEspera pacienteUrgenciaAlta = gestionClinicaService.registrarPaciente(
                new CrearPacienteRequest("2-7", "Paciente Alta", "Hospital A", "Cardiologia", 5)
        );

        CupoAgenda cupo = gestionClinicaService.crearCupo(
                new CrearCupoRequest("Hospital A", "Cardiologia", LocalDateTime.now().plusDays(1))
        );

        AsignacionResponse respuesta = gestionClinicaService.reasignarCupoDisponible(cupo.getId());

        Assertions.assertEquals(pacienteUrgenciaAlta.getId(), respuesta.pacienteId());
        Assertions.assertEquals("CONFIRMADA", respuesta.estadoNotificacion());
        Assertions.assertEquals(pacienteUrgenciaAlta.getNombre(), respuesta.pacienteNombre());

        PacienteListaEspera altaActualizada = gestionClinicaService.listarPacientesEnEspera().stream()
                .filter(p -> p.getId().equals(pacienteUrgenciaAlta.getId()))
                .findFirst()
                .orElseThrow();

        PacienteListaEspera bajaActualizada = gestionClinicaService.listarPacientesEnEspera().stream()
                .filter(p -> p.getId().equals(pacienteUrgenciaBaja.getId()))
                .findFirst()
                .orElseThrow();

        Assertions.assertEquals(EstadoListaEspera.ASIGNADO, altaActualizada.getEstado());
        Assertions.assertEquals(EstadoListaEspera.EN_ESPERA, bajaActualizada.getEstado());

        CupoAgenda cupoActualizado = gestionClinicaService.listarCupos().stream()
                .filter(c -> c.getId().equals(cupo.getId()))
                .findFirst()
                .orElseThrow();

        Assertions.assertEquals(EstadoCupoAgenda.RESERVADO, cupoActualizado.getEstado());
        Assertions.assertEquals(pacienteUrgenciaAlta.getId(), cupoActualizado.getPacienteId());
        Assertions.assertEquals(EstadoNotificacion.CONFIRMADA, cupoActualizado.getEstadoNotificacion());
    }
}
