package com.activeage.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AgendamentoRequestDTO(
        List<LocalDateTime> horarios,
        BigDecimal valor,
        Integer duracaoMinutos
) {}