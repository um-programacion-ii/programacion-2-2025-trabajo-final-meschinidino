package com.eventos.sistemaeventos.presentation.dto;

import com.eventos.sistemaeventos.application.dto.catedra.EventoCatedraDTO;
import lombok.Data;

@Data
public class SyncWebhookRequestDTO {
    private String tipoCambio;
    private EventoCatedraDTO evento;
}
