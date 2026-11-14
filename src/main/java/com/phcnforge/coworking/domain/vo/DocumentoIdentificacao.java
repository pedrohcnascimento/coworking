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
public class DocumentoIdentificacao {

    @Column(name = "documento", nullable = false, unique = true, length = 14)
    private String numero;

    public DocumentoIdentificacao(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new RegraNegocioException("O documento de identificação é obrigatório.");
        }
        String apenasDigitos = numero.replaceAll("\\D", "");
        if (apenasDigitos.length() != 11 && apenasDigitos.length() != 14) {
            throw new RegraNegocioException("O documento deve conter 11 dígitos (CPF) ou 14 dígitos (CNPJ).");
        }
        this.numero = apenasDigitos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentoIdentificacao that = (DocumentoIdentificacao) o;
        return Objects.equals(numero, that.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return numero;
    }
}
