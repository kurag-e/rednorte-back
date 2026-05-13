package cl.rednorte.ms_gestion_clinica.controller;

import cl.rednorte.ms_gestion_clinica.dto.AsignacionResponse;
import cl.rednorte.ms_gestion_clinica.dto.CrearCupoRequest;
import cl.rednorte.ms_gestion_clinica.dto.CrearPacienteRequest;
import cl.rednorte.ms_gestion_clinica.model.CupoAgenda;
import cl.rednorte.ms_gestion_clinica.model.PacienteListaEspera;
import cl.rednorte.ms_gestion_clinica.service.GestionClinicaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clinica")
public class GestionClinicaController {

    private final GestionClinicaService gestionClinicaService;

    public GestionClinicaController(GestionClinicaService gestionClinicaService) {
        this.gestionClinicaService = gestionClinicaService;
    }

    @PostMapping("/lista-espera")
    @ResponseStatus(HttpStatus.CREATED)
    public PacienteListaEspera registrarPaciente(@Valid @RequestBody CrearPacienteRequest request) {
        return gestionClinicaService.registrarPaciente(request);
    }

    @GetMapping("/lista-espera")
    public List<PacienteListaEspera> listarPacientes() {
        return gestionClinicaService.listarPacientesEnEspera();
    }

    @PostMapping("/agenda/cupos")
    @ResponseStatus(HttpStatus.CREATED)
    public CupoAgenda crearCupo(@Valid @RequestBody CrearCupoRequest request) {
        return gestionClinicaService.crearCupo(request);
    }

    @GetMapping("/agenda/cupos")
    public List<CupoAgenda> listarCupos() {
        return gestionClinicaService.listarCupos();
    }

    @PostMapping("/agenda/cupos/{cupoId}/reasignar")
    public AsignacionResponse reasignar(@PathVariable Long cupoId) {
        return gestionClinicaService.reasignarCupoDisponible(cupoId);
    }

    @PostMapping("/agenda/cupos/{cupoId}/cancelacion")
    public AsignacionResponse registrarCancelacion(@PathVariable Long cupoId) {
        return gestionClinicaService.registrarCancelacionYReasignar(cupoId);
    }
}
