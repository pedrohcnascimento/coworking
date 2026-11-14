package com.phcnforge.coworking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Schema(description = "Dados para cadastro ou atualização de uma estação de trabalho")
public record EstacaoDeTrabalhoRequestDTO(
        @NotBlank(message = "O código da estação é obrigatório.")
        @Schema(example = "DESK-A01")
        String codigo,

        @NotBlank(message = "A descrição é obrigatória.")
        @Schema(example = "Mesa individual próximo à janela - 1º andar")
        String descricao,

        @NotNull(message = "A capacidade máxima é obrigatória.")
        @Positive(message = "A capacidade deve ser maior que zero.")
        @Schema(example = "1")
        Integer capacidadeMaxima,

        @NotNull(message = "O valor por hora é obrigatório.")
        @Positive(message = "O valor por hora deve ser maior que zero.")
        @Schema(example = "25.00")
        BigDecimal valorPorHora,

        @Schema(example = "true")
        Boolean ativa) {}
