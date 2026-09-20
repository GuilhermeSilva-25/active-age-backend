package com.activeage.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConfirmarPagamentoDTO(
        String transacaoId,
        String metodo,
        BigDecimal valorPago,
        LocalDateTime dataPagamento
) {}