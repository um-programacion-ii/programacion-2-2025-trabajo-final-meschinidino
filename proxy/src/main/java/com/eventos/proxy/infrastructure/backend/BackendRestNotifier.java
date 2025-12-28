package com.eventos.proxy.infrastructure.backend;

import com.eventos.proxy.application.port.out.BackendNotifier;
import com.eventos.proxy.security.ServiceJwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class BackendRestNotifier implements BackendNotifier {

    private final RestTemplate restTemplate;
    private final String backendUrl;
    private final String webhookToken;
    private final ServiceJwtProvider serviceJwtProvider;

    public BackendRestNotifier(
            RestTemplate restTemplate,
            @Value("${backend.url}") String backendUrl,
            @Value("${backend.webhook-token}") String webhookToken,
            ServiceJwtProvider serviceJwtProvider) {
        this.restTemplate = restTemplate;
        this.backendUrl = backendUrl;
        this.webhookToken = webhookToken;
        this.serviceJwtProvider = serviceJwtProvider;
    }

    @Override
    public void notificarEvento(String mensaje) {
        try {
            String url = backendUrl + "/api/sync/webhook";
            log.debug("Notificando al backend: {}", url);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.setBearerAuth(serviceJwtProvider.createToken());
            if (webhookToken != null && !webhookToken.isBlank()) {
                headers.set("X-Webhook-Token", webhookToken);
            }

            org.springframework.http.HttpEntity<String> request = new org.springframework.http.HttpEntity<>(mensaje,
                    headers);

            restTemplate.postForEntity(url, request, String.class);
            log.info("Backend notificado exitosamente");
        } catch (Exception e) {
            log.error("Error notificando backend: {}", e.getMessage());
        }
    }
}
