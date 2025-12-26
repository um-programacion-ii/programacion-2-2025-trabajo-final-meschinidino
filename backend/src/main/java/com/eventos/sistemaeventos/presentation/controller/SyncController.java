package com.eventos.sistemaeventos.presentation.controller;

import com.eventos.sistemaeventos.application.service.EventoService;
import com.eventos.sistemaeventos.presentation.dto.SyncWebhookRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final EventoService eventoService;
    private final String webhookToken;

    public SyncController(EventoService eventoService, @Value("${sync.webhook.token}") String webhookToken) {
        this.eventoService = eventoService;
        this.webhookToken = webhookToken;
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> recibirWebhook(
            @RequestHeader(value = "X-Webhook-Token", required = false) String token,
            @RequestBody SyncWebhookRequestDTO request) {
        if (webhookToken == null || webhookToken.isBlank()) {
            log.error("sync.webhook.token no configurado");
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "webhook no configurado");
            return ResponseEntity.internalServerError().body(error);
        }

        if (token == null || !webhookToken.equals(token)) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "token inválido");
            return ResponseEntity.status(401).body(error);
        }

        if (request.getTipoCambio() == null || request.getTipoCambio().isBlank()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "tipo_cambio es obligatorio");
            return ResponseEntity.badRequest().body(error);
        }

        if (request.getEvento() == null || request.getEvento().getId() == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "datos del evento son obligatorios");
            return ResponseEntity.badRequest().body(error);
        }

        log.info("Webhook recibido: tipoCambio={}, eventoId={}", request.getTipoCambio(), request.getEvento().getId());

        eventoService.procesarCambioEvento(request.getTipoCambio(), request.getEvento());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Webhook procesado");
        return ResponseEntity.ok(response);
    }
}
