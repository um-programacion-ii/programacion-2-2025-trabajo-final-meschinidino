package com.eventos.sistemaeventos.presentation.controller;

import com.eventos.sistemaeventos.application.service.EventoService;
import com.eventos.sistemaeventos.infrastructure.security.ServiceJwtProvider;
import com.eventos.sistemaeventos.presentation.dto.SyncWebhookRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
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
    private final ServiceJwtProvider serviceJwtProvider;

    public SyncController(
            EventoService eventoService,
            @Value("${sync.webhook.token}") String webhookToken,
            ServiceJwtProvider serviceJwtProvider) {
        this.eventoService = eventoService;
        this.webhookToken = webhookToken;
        this.serviceJwtProvider = serviceJwtProvider;
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> recibirWebhook(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestHeader(value = "X-Webhook-Token", required = false) String token,
            @RequestBody SyncWebhookRequestDTO request) {
        if (!isAuthorized(authorization, token)) {
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

    private boolean isAuthorized(String authorization, String token) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String bearer = authorization.substring("Bearer ".length());
            if (serviceJwtProvider.isValid(bearer)) {
                return true;
            }
        }

        if (webhookToken != null && !webhookToken.isBlank()) {
            return token != null && webhookToken.equals(token);
        }

        return false;
    }
}
