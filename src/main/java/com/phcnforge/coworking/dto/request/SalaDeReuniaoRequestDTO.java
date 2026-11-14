package com.phcnforge.coworking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Schema(description = "Dados para cadastro de uma sala de reunião")
public record SalaDeReuniaoRequestDTO(
        @NotBlank(message = "O nome da sala é obrigatório.")
        @Schema(example = "Sala Inovação")
        String nome,

        @NotNull(message = "O andar é obrigatório.")
        @Schema(example = "2")
        Integer andar,

        @Schema(example = "true")
        Boolean possuiProjetor,

        @NotNull(message = "A capacidade máxima é obrigatória.")
        @Positive(message = "A capacidade deve ser maior que zero.")
        @Schema(example = "8")
        Integer capacidadeMaxima,

        @NotNull(message = "O valor por hora é obrigatório.")
        @Positive(message = "O valor por hora deve ser maior que zero.")
        @Schema(example = "80.00")
        BigDecimal valorPorHora,

        @Schema(example = "true")
        Boolean ativa) {}
