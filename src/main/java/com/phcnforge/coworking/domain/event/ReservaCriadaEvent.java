package com.phcnforge.coworking.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservaCriadaEvent(
        Long reservaId,
        Long usuarioId,
        Long estacaoId,
        LocalDateTime inicio,
        LocalDateTime fim,
        BigDecimal valorTotal) {}
