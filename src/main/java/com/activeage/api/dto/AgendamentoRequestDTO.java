package com.activeage.api.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AgendamentoRequestDTO(List<LocalDateTime> horarios) {}