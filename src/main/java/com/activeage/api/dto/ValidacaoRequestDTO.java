package com.activeage.api.dto;

import com.activeage.api.enums.StatusValidacao;

/**
 * DTO para o Admin aprovar ou reprovar o médico, contendo o status final e a mensagem.
 */
public record ValidacaoRequestDTO(StatusValidacao status, String mensagem) {}