package cl.rednorte.ms_gestion_clinica.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HospitalPerifericoClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HospitalPerifericoClient.class);

    private final RestClient restClient;

    @Value("${hospital.periferico.url:http://localhost:8099}")
    private String hospitalPerifericoUrl;

    public HospitalPerifericoClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @CircuitBreaker(name = "hospitalPeriferico", fallbackMethod = "fallbackNotificacion")
    public boolean notificarAsignacion(Long cupoId, Long pacienteId, String hospital) {
        HttpStatusCode status = restClient.post()
                .uri(hospitalPerifericoUrl + "/api/notificaciones/agenda")
                .body(new NotificacionPayload(cupoId, pacienteId, hospital))
                .retrieve()
                .toBodilessEntity()
                .getStatusCode();
        return status.is2xxSuccessful();
    }

    private boolean fallbackNotificacion(Long cupoId, Long pacienteId, String hospital, Throwable ex) {
        LOGGER.warn("Circuit breaker activado para hospital periferico. cupoId={}, pacienteId={}, causa={}",
                cupoId, pacienteId, ex.getMessage());
        return false;
    }

    private record NotificacionPayload(Long cupoId, Long pacienteId, String hospital) {
    }
}
