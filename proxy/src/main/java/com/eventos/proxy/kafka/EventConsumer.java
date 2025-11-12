package com.eventos.proxy.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer de Kafka que escucha eventos del sistema
 */
@Slf4j
@Component
public class EventConsumer {

    /**
     * Escucha eventos del sistema
     */
    @KafkaListener(topics = "eventos", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeEvent(String message) {
        log.info("Evento recibido de Kafka: {}", message);
        // Aquí se puede procesar el evento según sea necesario
        // Por ejemplo: enviar notificaciones, actualizar cache, etc.
    }

    /**
     * Escucha notificaciones
     */
    @KafkaListener(topics = "notificaciones", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeNotification(String message) {
        log.info("Notificación recibida de Kafka: {}", message);
        // Procesar notificación
    }
}

