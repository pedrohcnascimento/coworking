package com.phcnforge.coworking.domain.event;

import java.time.LocalDateTime;

public record ReservaCanceladaEvent(
        Long reservaId,
        Long usuarioId,
        Long estacaoId,
        LocalDateTime canceladoEm) {}
