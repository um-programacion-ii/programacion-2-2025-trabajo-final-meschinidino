package com.eventos.proxy.application.service;

import com.eventos.proxy.application.port.in.BackendNotificationUseCase;
import com.eventos.proxy.application.port.out.BackendNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BackendNotificationService implements BackendNotificationUseCase {

    private final BackendNotifier backendNotifier;

    public BackendNotificationService(BackendNotifier backendNotifier) {
        this.backendNotifier = backendNotifier;
    }

    @Override
    public void notificarEvento(String mensaje) {
        log.info("Notificando al backend");
        backendNotifier.notificarEvento(mensaje);
    }
}
