package com.phcnforge.coworking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "Dados para solicitação de uma nova reserva de estação de trabalho")
public record ReservaRequestDTO(
        @NotNull(message = "O ID do usuário é obrigatório.")
        @Schema(example = "1")
        Long usuarioId,

        @NotNull(message = "O ID da estação de trabalho é obrigatório.")
        @Schema(example = "1")
        Long estacaoId,

        @NotNull(message = "A data/hora de início é obrigatória.")
        @Schema(example = "2026-11-20T09:00:00")
        LocalDateTime inicio,

        @NotNull(message = "A data/hora de fim é obrigatória.")
        @Schema(example = "2026-11-20T13:00:00")
        LocalDateTime fim) {}
