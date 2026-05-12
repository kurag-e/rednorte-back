package cl.rednorte.ms_gestion_clinica.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "pacientes_lista_espera")
public class PacienteListaEspera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rut", nullable = false, unique = true, length = 20)
    private String rut;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "hospital_referencia", nullable = false, length = 80)
    private String hospitalReferencia;

    @Column(name = "especialidad", nullable = false, length = 80)
    private String especialidad;

    @Column(name = "nivel_urgencia", nullable = false)
    private Integer nivelUrgencia;

    @Column(name = "puntaje_prioridad", nullable = false)
    private Integer puntajePrioridad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoListaEspera estado;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    @Column(name = "cupo_agenda_id")
    private Long cupoAgendaId;

    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoListaEspera.EN_ESPERA;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHospitalReferencia() {
        return hospitalReferencia;
    }

    public void setHospitalReferencia(String hospitalReferencia) {
        this.hospitalReferencia = hospitalReferencia;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public Integer getNivelUrgencia() {
        return nivelUrgencia;
    }

    public void setNivelUrgencia(Integer nivelUrgencia) {
        this.nivelUrgencia = nivelUrgencia;
    }

    public Integer getPuntajePrioridad() {
        return puntajePrioridad;
    }

    public void setPuntajePrioridad(Integer puntajePrioridad) {
        this.puntajePrioridad = puntajePrioridad;
    }

    public EstadoListaEspera getEstado() {
        return estado;
    }

    public void setEstado(EstadoListaEspera estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public Long getCupoAgendaId() {
        return cupoAgendaId;
    }

    public void setCupoAgendaId(Long cupoAgendaId) {
        this.cupoAgendaId = cupoAgendaId;
    }
}
