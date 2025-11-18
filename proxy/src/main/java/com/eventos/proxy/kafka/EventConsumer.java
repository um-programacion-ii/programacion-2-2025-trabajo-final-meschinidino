package com.eventos.proxy.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Consumer de Kafka que escucha eventos del sistema
 */
@Slf4j
@Component
public class EventConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${backend.url}")
    private String backendUrl;

    /**
     * Escucha eventos del sistema desde Kafka de cátedra
     */
    @KafkaListener(topics = "eventos", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeEvent(String message) {
        log.info("✅ Evento recibido de Kafka: {}", message);

        // Notificar al backend sobre el cambio
        notificarBackend(message);
    }

    /**
     * Notifica al backend sobre cambios en eventos
     */
    private void notificarBackend(String mensaje) {
        try {
            String url = backendUrl + "/api/eventos/sincronizar";
            log.debug("Notificando al backend: {}", url);

            restTemplate.postForEntity(url, mensaje, String.class);
            log.info("✅ Backend notificado exitosamente");

        } catch (Exception e) {
            log.error("❌ Error notificando backend: {}", e.getMessage());
            // TODO: Implementar retry logic si es necesario
        }
    }

    /**
     * Listener adicional para notificaciones (opcional)
     */
    @KafkaListener(topics = "notificaciones", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeNotification(String message) {
        log.info("📧 Notificación recibida: {}", message);
        // Procesar notificación si es necesario
    }
}

