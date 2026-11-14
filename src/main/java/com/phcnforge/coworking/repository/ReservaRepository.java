package com.phcnforge.coworking.repository;

import com.phcnforge.coworking.domain.model.Reserva;
import com.phcnforge.coworking.domain.model.StatusReserva;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByEstacaoIdAndStatus(Long estacaoId, StatusReserva status);

    List<Reserva> findByUsuarioId(Long usuarioId);

    @Query("""
            SELECT r FROM Reserva r
            WHERE r.usuario.id = :usuarioId
              AND r.status = :status
              AND r.intervalo.inicio >= :inicioSemana
              AND r.intervalo.fim <= :fimSemana
            """)
    List<Reserva> buscarReservasDoUsuarioNoPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("status") StatusReserva status,
            @Param("inicioSemana") LocalDateTime inicioSemana,
            @Param("fimSemana") LocalDateTime fimSemana);
}
