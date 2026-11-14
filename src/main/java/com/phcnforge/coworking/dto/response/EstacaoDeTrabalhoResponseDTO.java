package com.phcnforge.coworking.dto.response;

import com.phcnforge.coworking.domain.model.EstacaoDeTrabalho;
import java.math.BigDecimal;

public record EstacaoDeTrabalhoResponseDTO(
        Long id,
        String codigo,
        String descricao,
        Integer capacidadeMaxima,
        BigDecimal valorPorHora,
        boolean ativa) {

    public static EstacaoDeTrabalhoResponseDTO deEntidade(EstacaoDeTrabalho estacao) {
        return new EstacaoDeTrabalhoResponseDTO(
                estacao.getId(),
                estacao.getCodigo(),
                estacao.getDescricao(),
                estacao.getCapacidade() != null ? estacao.getCapacidade().getMaximoPessoas() : null,
                estacao.getTarifa() != null ? estacao.getTarifa().getValorPorHora() : null,
                estacao.isAtiva());
    }
}
