package com.eventos.proxy.infrastructure.messaging;

import com.eventos.proxy.application.port.in.BackendNotificationUseCase;
import com.eventos.proxy.application.port.in.CatedraProxyUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventConsumer {

    private final BackendNotificationUseCase backendNotificationUseCase;
    private final CatedraProxyUseCase catedraProxyUseCase;
    private final ObjectMapper objectMapper;

    public EventConsumer(
            BackendNotificationUseCase backendNotificationUseCase,
            CatedraProxyUseCase catedraProxyUseCase,
            ObjectMapper objectMapper) {
        this.backendNotificationUseCase = backendNotificationUseCase;
        this.catedraProxyUseCase = catedraProxyUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "eventos", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeEvent(String message) {
        log.info("Evento recibido de Kafka: {}", message);
        String payload = buildWebhookPayload(message);
        if (payload == null) {
            log.warn("No se pudo construir payload de webhook, se omite el mensaje.");
            return;
        }
        backendNotificationUseCase.notificarEvento(payload);
    }

    @KafkaListener(topics = "notificaciones", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeNotification(String message) {
        log.info("Notificacion recibida: {}", message);
    }

    private String buildWebhookPayload(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String tipoCambio = extractTipoCambio(root);
            JsonNode eventoNode = extractEventoNode(root);

            if (eventoNode == null) {
                log.warn("Payload Kafka sin datos de evento: {}", message);
                return null;
            }

            if (!eventoNode.hasNonNull("id")) {
                Long eventoId = extractEventoId(root);
                if (eventoId != null) {
                    JsonNode fetched = objectMapper.valueToTree(catedraProxyUseCase.obtenerEvento(eventoId));
                    if (fetched.hasNonNull("id")) {
                        eventoNode = fetched;
                    }
                }
            }

            if (!eventoNode.hasNonNull("id")) {
                log.warn("No se pudo obtener un evento válido para el webhook.");
                return null;
            }

            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("tipoCambio", tipoCambio);
            payload.set("evento", eventoNode);
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            log.error("Error parseando evento Kafka: {}", e.getMessage());
            return null;
        }
    }

    private String extractTipoCambio(JsonNode root) {
        if (root.hasNonNull("tipoCambio")) {
            return root.get("tipoCambio").asText();
        }
        if (root.hasNonNull("tipo_cambio")) {
            return root.get("tipo_cambio").asText();
        }
        if (root.hasNonNull("tipo")) {
            return root.get("tipo").asText();
        }
        return "UPDATE";
    }

    private JsonNode extractEventoNode(JsonNode root) {
        if (root.hasNonNull("evento")) {
            return root.get("evento");
        }
        if (root.hasNonNull("event")) {
            return root.get("event");
        }
        if (root.hasNonNull("id")) {
            return root;
        }
        return null;
    }

    private Long extractEventoId(JsonNode root) {
        if (root.hasNonNull("eventoId")) {
            return root.get("eventoId").asLong();
        }
        if (root.hasNonNull("evento_id")) {
            return root.get("evento_id").asLong();
        }
        if (root.hasNonNull("id")) {
            return root.get("id").asLong();
        }
        return null;
    }
}
