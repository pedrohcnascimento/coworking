package com.phcnforge.coworking.domain.vo;

import com.phcnforge.coworking.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tarifa {

    @Column(name = "valor_por_hora", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPorHora;

    public Tarifa(BigDecimal valorPorHora) {
        if (valorPorHora == null || valorPorHora.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor da tarifa por hora deve ser maior que zero.");
        }
        this.valorPorHora = valorPorHora.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularTotal(long horas) {
        if (horas <= 0) {
            throw new RegraNegocioException("A quantidade de horas para cálculo deve ser positiva.");
        }
        return valorPorHora.multiply(BigDecimal.valueOf(horas)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tarifa tarifa = (Tarifa) o;
        return valorPorHora != null && valorPorHora.compareTo(tarifa.valorPorHora) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valorPorHora != null ? valorPorHora.stripTrailingZeros() : null);
    }
}
