package com.phcnforge.coworking.service;

import com.phcnforge.coworking.domain.event.ReservaCanceladaEvent;
import com.phcnforge.coworking.domain.event.ReservaCriadaEvent;
import com.phcnforge.coworking.domain.model.EstacaoDeTrabalho;
import com.phcnforge.coworking.domain.model.Reserva;
import com.phcnforge.coworking.domain.model.StatusReserva;
import com.phcnforge.coworking.domain.model.Usuario;
import com.phcnforge.coworking.domain.vo.IntervaloDeTempo;
import com.phcnforge.coworking.dto.request.ReservaRequestDTO;
import com.phcnforge.coworking.dto.response.ReservaResponseDTO;
import com.phcnforge.coworking.exception.ConflitoReservaException;
import com.phcnforge.coworking.exception.RecursoNaoEncontradoException;
import com.phcnforge.coworking.repository.ReservaRepository;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioService usuarioService;
    private final EstacaoDeTrabalhoService estacaoService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ReservaResponseDTO criarReserva(ReservaRequestDTO dto) {
        IntervaloDeTempo intervalo = new IntervaloDeTempo(dto.inicio(), dto.fim());
        Usuario usuario = usuarioService.buscarEntidadePorId(dto.usuarioId());
        EstacaoDeTrabalho estacao = estacaoService.buscarEntidadePorId(dto.estacaoId());

        long horasNaSemana = calcularHorasReservadasNaSemana(usuario.getId(), intervalo.getInicio());
        usuario.validarAptidaoParaReserva(horasNaSemana, intervalo.duracaoEmHoras());

        List<Reserva> reservasAtivasEstacao =
                reservaRepository.findByEstacaoIdAndStatus(estacao.getId(), StatusReserva.CONFIRMADA);

        boolean possuiConflito = reservasAtivasEstacao.stream()
                .anyMatch(existente -> existente.getIntervalo().sobrepoe(intervalo));

        if (possuiConflito) {
            throw new ConflitoReservaException(
                    "A estação de trabalho já possui uma reserva confirmada que se sobrepõe ao horário informado.");
        }

        Reserva novaReserva = Reserva.criar(usuario, estacao, intervalo);
        Reserva salva = reservaRepository.save(novaReserva);

        eventPublisher.publishEvent(new ReservaCriadaEvent(
                salva.getId(),
                usuario.getId(),
                estacao.getId(),
                intervalo.getInicio(),
                intervalo.getFim(),
                salva.getValorTotal()));

        return ReservaResponseDTO.deEntidade(salva);
    }

    @Transactional
    public ReservaResponseDTO cancelarReserva(Long id) {
        return cancelarReserva(id, LocalDateTime.now());
    }

    @Transactional
    public ReservaResponseDTO cancelarReserva(Long id, LocalDateTime momentoCancelamento) {
        Reserva reserva = buscarEntidadePorId(id);
        reserva.cancelar(momentoCancelamento, Reserva.ANTECEDENCIA_MINIMA_CANCELAMENTO_HORAS);
        Reserva salva = reservaRepository.save(reserva);

        eventPublisher.publishEvent(new ReservaCanceladaEvent(
                salva.getId(),
                salva.getUsuario().getId(),
                salva.getEstacao().getId(),
                momentoCancelamento));

        return ReservaResponseDTO.deEntidade(salva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarTodas() {
        return reservaRepository.findAll().stream()
                .map(ReservaResponseDTO::deEntidade)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId).stream()
                .map(ReservaResponseDTO::deEntidade)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponseDTO buscarPorId(Long id) {
        return ReservaResponseDTO.deEntidade(buscarEntidadePorId(id));
    }

    @Transactional
    public void deletar(Long id) {
        Reserva reserva = buscarEntidadePorId(id);
        reservaRepository.delete(reserva);
    }

    private Reserva buscarEntidadePorId(Long id) {
        return reservaRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada com ID: " + id));
    }

    private long calcularHorasReservadasNaSemana(Long usuarioId, LocalDateTime dataReferencia) {
        LocalDateTime inicioSemana = dataReferencia
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .with(LocalTime.MIN);
        LocalDateTime fimSemana = dataReferencia
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .with(LocalTime.MAX);

        List<Reserva> reservasDaSemana = reservaRepository.buscarReservasDoUsuarioNoPeriodo(
                usuarioId, StatusReserva.CONFIRMADA, inicioSemana, fimSemana);

        return reservasDaSemana.stream()
                .mapToLong(r -> r.getIntervalo().duracaoEmHoras())
                .sum();
    }
}
