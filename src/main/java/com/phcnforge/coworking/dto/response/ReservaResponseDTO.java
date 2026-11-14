package com.phcnforge.coworking.dto.response;

import com.phcnforge.coworking.domain.model.Reserva;
import com.phcnforge.coworking.domain.model.StatusReserva;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservaResponseDTO(
        Long id,
        Long usuarioId,
        String nomeUsuario,
        Long estacaoId,
        String codigoEstacao,
        LocalDateTime inicio,
        LocalDateTime fim,
        BigDecimal valorTotal,
        StatusReserva status,
        LocalDateTime criadoEm) {

    public static ReservaResponseDTO deEntidade(Reserva reserva) {
        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getUsuario().getId(),
                reserva.getUsuario().getNome(),
                reserva.getEstacao().getId(),
                reserva.getEstacao().getCodigo(),
                reserva.getIntervalo().getInicio(),
                reserva.getIntervalo().getFim(),
                reserva.getValorTotal(),
                reserva.getStatus(),
                reserva.getCriadoEm());
    }
}
