package com.phcnforge.coworking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação ou atualização completa de um usuário")
public record UsuarioRequestDTO(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
        @Schema(example = "Ana Carolina Silva")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Schema(example = "ana.silva@email.com")
        String email,

        @NotBlank(message = "O documento (CPF ou CNPJ) é obrigatório.")
        @Schema(example = "12345678909")
        String documento,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
        @Schema(example = "senhaSegura123")
        String senha,

        @Schema(example = "false")
        Boolean inadimplente,

        @Schema(example = "true")
        Boolean ativo,

        @Min(value = 1, message = "O limite de horas semanais deve ser de pelo menos 1 hora.")
        @Schema(example = "40")
        Integer limiteHorasSemanais) {}
