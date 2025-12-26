package com.eventos.proxy.infrastructure.messaging;

import com.eventos.proxy.application.port.in.BackendNotificationUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventConsumerTest {

    @Mock
    private BackendNotificationUseCase backendNotificationUseCase;

    @Test
    void consumeEvent_notificaBackend() {
        EventConsumer consumer = new EventConsumer(backendNotificationUseCase);

        consumer.consumeEvent("evento");

        verify(backendNotificationUseCase).notificarEvento("evento");
    }
}
