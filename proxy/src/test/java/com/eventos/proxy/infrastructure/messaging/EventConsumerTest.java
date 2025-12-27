package com.eventos.proxy.infrastructure.messaging;

import com.eventos.proxy.application.port.in.BackendNotificationUseCase;
import com.eventos.proxy.application.port.in.CatedraProxyUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventConsumerTest {

    @Mock
    private BackendNotificationUseCase backendNotificationUseCase;

    @Mock
    private CatedraProxyUseCase catedraProxyUseCase;

    @Test
    void consumeEvent_notificaBackend() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        EventConsumer consumer = new EventConsumer(backendNotificationUseCase, catedraProxyUseCase, objectMapper);

        consumer.consumeEvent("{\"tipoCambio\":\"UPDATE\",\"evento\":{\"id\":1}}");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(backendNotificationUseCase).notificarEvento(captor.capture());

        JsonNode node = objectMapper.readTree(captor.getValue());
        assertThat(node.get("tipoCambio").asText()).isEqualTo("UPDATE");
        assertThat(node.get("evento").get("id").asLong()).isEqualTo(1L);
    }
}
