package com.activeage.api.dto;

import java.math.BigDecimal;

public record ConfirmarPagamentoDTO(
        String transacaoId,
        String metodo,
        BigDecimal valorPago
) {}