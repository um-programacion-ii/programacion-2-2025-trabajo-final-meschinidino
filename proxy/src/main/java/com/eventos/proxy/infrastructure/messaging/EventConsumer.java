package com.eventos.proxy.infrastructure.messaging;

import com.eventos.proxy.application.port.in.BackendNotificationUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventConsumer {

    private final BackendNotificationUseCase backendNotificationUseCase;

    public EventConsumer(BackendNotificationUseCase backendNotificationUseCase) {
        this.backendNotificationUseCase = backendNotificationUseCase;
    }

    @KafkaListener(topics = "eventos", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeEvent(String message) {
        log.info("Evento recibido de Kafka: {}", message);
        backendNotificationUseCase.notificarEvento(message);
    }

    @KafkaListener(topics = "notificaciones", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeNotification(String message) {
        log.info("Notificacion recibida: {}", message);
    }
}
