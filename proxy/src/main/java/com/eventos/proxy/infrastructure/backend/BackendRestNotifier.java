package com.eventos.proxy.infrastructure.backend;

import com.eventos.proxy.application.port.out.BackendNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class BackendRestNotifier implements BackendNotifier {

    private final RestTemplate restTemplate;
    private final String backendUrl;

    public BackendRestNotifier(
            RestTemplate restTemplate,
            @Value("${backend.url}") String backendUrl) {
        this.restTemplate = restTemplate;
        this.backendUrl = backendUrl;
    }

    @Override
    public void notificarEvento(String mensaje) {
        try {
            String url = backendUrl + "/api/eventos/sincronizar";
            log.debug("Notificando al backend: {}", url);
            restTemplate.postForEntity(url, mensaje, String.class);
            log.info("Backend notificado exitosamente");
        } catch (Exception e) {
            log.error("Error notificando backend: {}", e.getMessage());
        }
    }
}
