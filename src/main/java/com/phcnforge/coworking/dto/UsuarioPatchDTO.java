package com.phcnforge.coworking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualização parcial (PATCH) de um usuário")
public record UsuarioPatchDTO(
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
        @Schema(example = "Ana Carolina S. Oliveira")
        String nome,

        @Schema(example = "ana.oliveira@email.com")
        String email,

        @Schema(example = "12345678909")
        String documento,

        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
        @Schema(example = "novaSenha123")
        String senha,

        @Schema(example = "false")
        Boolean inadimplente,

        @Schema(example = "true")
        Boolean ativo,

        @Min(value = 1, message = "O limite de horas semanais deve ser de pelo menos 1 hora.")
        @Schema(example = "30")
        Integer limiteHorasSemanais) {}
