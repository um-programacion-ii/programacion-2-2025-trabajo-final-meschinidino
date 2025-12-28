package com.eventos.proxy.application.port.out;

public interface BackendNotifier {

    void notificarEvento(String mensaje);
}
