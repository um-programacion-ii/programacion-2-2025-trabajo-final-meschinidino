package com.eventos.proxy.application.service;

import com.eventos.proxy.application.port.out.BackendNotifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BackendNotificationServiceTest {

    @Mock
    private BackendNotifier backendNotifier;

    @InjectMocks
    private BackendNotificationService backendNotificationService;

    @Test
    void notificarEvento_delegaEnNotifier() {
        backendNotificationService.notificarEvento("mensaje");

        verify(backendNotifier).notificarEvento("mensaje");
    }
}
