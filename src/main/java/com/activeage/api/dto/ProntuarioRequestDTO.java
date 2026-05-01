package com.activeage.api.dto;

public record ProntuarioRequestDTO(
        String agendamentoId,
        String queixaPrincipal,
        String diagnostico,
        String conduta,
        String receita,
        String atestado,
        String pedidoExames,
        boolean finalizar
) {}