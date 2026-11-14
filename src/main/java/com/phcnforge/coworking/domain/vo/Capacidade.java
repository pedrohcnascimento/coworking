package com.phcnforge.coworking.domain.vo;

import com.phcnforge.coworking.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Capacidade {

    @Column(name = "capacidade_maxima", nullable = false)
    private Integer maximoPessoas;

    public Capacidade(Integer maximoPessoas) {
        if (maximoPessoas == null || maximoPessoas < 1) {
            throw new RegraNegocioException("A capacidade máxima deve ser de pelo menos 1 pessoa.");
        }
        this.maximoPessoas = maximoPessoas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Capacidade that = (Capacidade) o;
        return Objects.equals(maximoPessoas, that.maximoPessoas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maximoPessoas);
    }
}
