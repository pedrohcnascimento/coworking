package com.phcnforge.coworking.domain.model;

import com.phcnforge.coworking.domain.vo.IntervaloDeTempo;
import com.phcnforge.coworking.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    public static final long ANTECEDENCIA_MINIMA_CANCELAMENTO_HORAS = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estacao_id", nullable = false)
    private EstacaoDeTrabalho estacao;

    @Embedded
    private IntervaloDeTempo intervalo;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusReserva status;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    public static Reserva criar(Usuario usuario, EstacaoDeTrabalho estacao, IntervaloDeTempo intervalo) {
        if (estacao == null || !estacao.isAtiva()) {
            throw new RegraNegocioException("A estação de trabalho selecionada está inativa ou indisponível.");
        }
        BigDecimal total = estacao.getTarifa().calcularTotal(intervalo.duracaoEmHoras());
        return Reserva.builder()
                .usuario(usuario)
                .estacao(estacao)
                .intervalo(intervalo)
                .valorTotal(total)
                .status(StatusReserva.CONFIRMADA)
                .criadoEm(LocalDateTime.now())
                .build();
    }

    public void cancelar(LocalDateTime momentoAtual, long horasMinimasAntecedencia) {
        if (this.status == StatusReserva.CANCELADA) {
            throw new RegraNegocioException("Esta reserva já se encontra cancelada.");
        }
        long minutosRestantes = Duration.between(momentoAtual, this.intervalo.getInicio()).toMinutes();
        if (minutosRestantes < horasMinimasAntecedencia * 60) {
            throw new RegraNegocioException(
                    String.format(
                            "O cancelamento só é permitido com pelo menos %d horas de antecedência.",
                            horasMinimasAntecedencia));
        }
        this.status = StatusReserva.CANCELADA;
    }
}
