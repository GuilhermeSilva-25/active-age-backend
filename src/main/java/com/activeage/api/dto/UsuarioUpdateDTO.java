package com.activeage.api.dto;

import java.math.BigDecimal;

public record UsuarioUpdateDTO(
        String nome,
        String telefone,
        String crm,
        String especializacao,
        BigDecimal valorConsulta,
        Integer duracaoMinutos
) {}