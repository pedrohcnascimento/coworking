package com.phcnforge.coworking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.phcnforge.coworking.domain.event.ReservaCanceladaEvent;
import com.phcnforge.coworking.domain.event.ReservaCriadaEvent;
import com.phcnforge.coworking.domain.model.EstacaoDeTrabalho;
import com.phcnforge.coworking.domain.model.Reserva;
import com.phcnforge.coworking.domain.model.StatusReserva;
import com.phcnforge.coworking.domain.model.Usuario;
import com.phcnforge.coworking.domain.vo.Capacidade;
import com.phcnforge.coworking.domain.vo.DocumentoIdentificacao;
import com.phcnforge.coworking.domain.vo.Email;
import com.phcnforge.coworking.domain.vo.IntervaloDeTempo;
import com.phcnforge.coworking.domain.vo.Tarifa;
import com.phcnforge.coworking.dto.request.ReservaRequestDTO;
import com.phcnforge.coworking.dto.response.ReservaResponseDTO;
import com.phcnforge.coworking.exception.ConflitoReservaException;
import com.phcnforge.coworking.exception.RegraNegocioException;
import com.phcnforge.coworking.repository.ReservaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private EstacaoDeTrabalhoService estacaoService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ReservaService reservaService;

    private Usuario usuario;
    private EstacaoDeTrabalho estacao;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("Mariana Costa")
                .email(new Email("mariana@email.com"))
                .documento(new DocumentoIdentificacao("98765432100"))
                .senha("hash")
                .inadimplente(false)
                .ativo(true)
                .limiteHorasSemanais(10)
                .build();

        estacao = EstacaoDeTrabalho.builder()
                .id(10L)
                .codigo("DESK-01")
                .descricao("Estação Individual")
                .capacidade(new Capacidade(1))
                .tarifa(new Tarifa(new BigDecimal("25.00")))
                .ativa(true)
                .build();
    }

    @Test
    @DisplayName("Deve criar reserva com sucesso, calcular tarifa e publicar evento")
    void deveCriarReservaComSucesso() {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 18, 9, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 11, 18, 13, 0);
        ReservaRequestDTO dto = new ReservaRequestDTO(1L, 10L, inicio, fim);

        when(usuarioService.buscarEntidadePorId(1L)).thenReturn(usuario);
        when(estacaoService.buscarEntidadePorId(10L)).thenReturn(estacao);
        when(reservaRepository.buscarReservasDoUsuarioNoPeriodo(
                        eq(1L), eq(StatusReserva.CONFIRMADA), any(), any()))
                .thenReturn(List.of());
        when(reservaRepository.findByEstacaoIdAndStatus(10L, StatusReserva.CONFIRMADA))
                .thenReturn(List.of());
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> {
            Reserva r = inv.getArgument(0);
            r.setId(100L);
            return r;
        });

        ReservaResponseDTO resposta = reservaService.criarReserva(dto);

        assertNotNull(resposta);
        assertEquals(100L, resposta.id());
        assertEquals(new BigDecimal("100.00"), resposta.valorTotal());
        assertEquals(StatusReserva.CONFIRMADA, resposta.status());
        verify(eventPublisher).publishEvent(any(ReservaCriadaEvent.class));
    }

    @Test
    @DisplayName("Deve impedir duas reservas sobrepostas na mesma estação")
    void deveImpedirReservasSobrepostas() {
        LocalDateTime inicioExistente = LocalDateTime.of(2026, 11, 18, 9, 0);
        LocalDateTime fimExistente = LocalDateTime.of(2026, 11, 18, 12, 0);
        Reserva existente = Reserva.criar(
                usuario, estacao, new IntervaloDeTempo(inicioExistente, fimExistente));

        ReservaRequestDTO dtoConflitante = new ReservaRequestDTO(
                1L,
                10L,
                LocalDateTime.of(2026, 11, 18, 11, 0),
                LocalDateTime.of(2026, 11, 18, 14, 0));

        when(usuarioService.buscarEntidadePorId(1L)).thenReturn(usuario);
        when(estacaoService.buscarEntidadePorId(10L)).thenReturn(estacao);
        when(reservaRepository.buscarReservasDoUsuarioNoPeriodo(
                        eq(1L), eq(StatusReserva.CONFIRMADA), any(), any()))
                .thenReturn(List.of());
        when(reservaRepository.findByEstacaoIdAndStatus(10L, StatusReserva.CONFIRMADA))
                .thenReturn(List.of(existente));

        assertThrows(
                ConflitoReservaException.class,
                () -> reservaService.criarReserva(dtoConflitante));
    }

    @Test
    @DisplayName("Deve bloquear reserva para usuário inadimplente")
    void deveBloquearUsuarioInadimplente() {
        usuario.setInadimplente(true);
        ReservaRequestDTO dto = new ReservaRequestDTO(
                1L,
                10L,
                LocalDateTime.of(2026, 11, 18, 9, 0),
                LocalDateTime.of(2026, 11, 18, 11, 0));

        when(usuarioService.buscarEntidadePorId(1L)).thenReturn(usuario);
        when(estacaoService.buscarEntidadePorId(10L)).thenReturn(estacao);
        when(reservaRepository.buscarReservasDoUsuarioNoPeriodo(
                        eq(1L), eq(StatusReserva.CONFIRMADA), any(), any()))
                .thenReturn(List.of());

        assertThrows(RegraNegocioException.class, () -> reservaService.criarReserva(dto));
    }

    @Test
    @DisplayName("Deve bloquear reserva quando ultrapassar o limite de horas semanais do membro")
    void deveBloquearQuandoExcederLimiteSemanal() {
        Reserva jaReservada = Reserva.criar(
                usuario,
                estacao,
                new IntervaloDeTempo(
                        LocalDateTime.of(2026, 11, 17, 9, 0),
                        LocalDateTime.of(2026, 11, 17, 17, 0))); // 8 horas já usadas (limite é 10)

        ReservaRequestDTO novaSolicitacao = new ReservaRequestDTO(
                1L,
                10L,
                LocalDateTime.of(2026, 11, 18, 9, 0),
                LocalDateTime.of(2026, 11, 18, 13, 0)); // +4 horas = 12h > 10h

        when(usuarioService.buscarEntidadePorId(1L)).thenReturn(usuario);
        when(estacaoService.buscarEntidadePorId(10L)).thenReturn(estacao);
        when(reservaRepository.buscarReservasDoUsuarioNoPeriodo(
                        eq(1L), eq(StatusReserva.CONFIRMADA), any(), any()))
                .thenReturn(List.of(jaReservada));

        assertThrows(RegraNegocioException.class, () -> reservaService.criarReserva(novaSolicitacao));
    }

    @Test
    @DisplayName("Deve permitir cancelamento apenas com pelo menos 2 horas de antecedência")
    void deveValidarAntecedenciaNoCancelamento() {
        LocalDateTime inicioReserva = LocalDateTime.of(2026, 11, 20, 14, 0);
        LocalDateTime fimReserva = LocalDateTime.of(2026, 11, 20, 16, 0);
        Reserva reserva = Reserva.criar(
                usuario, estacao, new IntervaloDeTempo(inicioReserva, fimReserva));
        reserva.setId(50L);

        when(reservaRepository.findById(50L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        // Tentativa faltando apenas 1 hora -> deve falhar
        LocalDateTime umaHoraAntes = LocalDateTime.of(2026, 11, 20, 13, 0);
        assertThrows(
                RegraNegocioException.class,
                () -> reservaService.cancelarReserva(50L, umaHoraAntes));

        // Tentativa faltando 3 horas -> deve cancelar com sucesso
        LocalDateTime tresHorasAntes = LocalDateTime.of(2026, 11, 20, 11, 0);
        ReservaResponseDTO cancelada = reservaService.cancelarReserva(50L, tresHorasAntes);
        assertEquals(StatusReserva.CANCELADA, cancelada.status());
        verify(eventPublisher).publishEvent(any(ReservaCanceladaEvent.class));
    }
}
