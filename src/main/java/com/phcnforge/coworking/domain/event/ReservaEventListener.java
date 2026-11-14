package com.phcnforge.coworking.domain.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ReservaEventListener {

    private static final Logger log = LoggerFactory.getLogger(ReservaEventListener.class);

    @EventListener
    public void aoCriarReserva(ReservaCriadaEvent event) {
        log.info(
                "Evento de domínio [ReservaCriadaEvent]: Reserva #{} confirmada para usuário #{} na estação #{} ({} até {}) - Valor R$ {}",
                event.reservaId(),
                event.usuarioId(),
                event.estacaoId(),
                event.inicio(),
                event.fim(),
                event.valorTotal());
    }

    @EventListener
    public void aoCancelarReserva(ReservaCanceladaEvent event) {
        log.info(
                "Evento de domínio [ReservaCanceladaEvent]: Reserva #{} do usuário #{} na estação #{} cancelada em {}",
                event.reservaId(),
                event.usuarioId(),
                event.estacaoId(),
                event.canceladoEm());
    }
}
