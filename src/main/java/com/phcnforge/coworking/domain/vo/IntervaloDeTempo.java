package com.phcnforge.coworking.domain.vo;

import com.phcnforge.coworking.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IntervaloDeTempo {

    public static final long DURACAO_MINIMA_MINUTOS = 60;
    public static final long DURACAO_MAXIMA_MINUTOS = 480; // 8 horas

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime inicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDateTime fim;

    public IntervaloDeTempo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            throw new RegraNegocioException("As datas de início e fim do intervalo são obrigatórias.");
        }
        if (!fim.isAfter(inicio)) {
            throw new RegraNegocioException("A data/hora de término deve ser posterior à data/hora de início.");
        }
        long minutos = Duration.between(inicio, fim).toMinutes();
        if (minutos < DURACAO_MINIMA_MINUTOS) {
            throw new RegraNegocioException("A reserva deve ter duração mínima de 1 hora.");
        }
        if (minutos > DURACAO_MAXIMA_MINUTOS) {
            throw new RegraNegocioException("A reserva não pode ultrapassar a duração máxima de 8 horas.");
        }
        this.inicio = inicio;
        this.fim = fim;
    }

    public long duracaoEmHoras() {
        long minutos = Duration.between(inicio, fim).toMinutes();
        return (minutos + 59) / 60;
    }

    public boolean sobrepoe(IntervaloDeTempo outro) {
        if (outro == null) {
            return false;
        }
        return this.inicio.isBefore(outro.getFim()) && outro.getInicio().isBefore(this.fim);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntervaloDeTempo that = (IntervaloDeTempo) o;
        return Objects.equals(inicio, that.inicio) && Objects.equals(fim, that.fim);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inicio, fim);
    }
}
