package com.phcnforge.coworking.dto.response;

import com.phcnforge.coworking.domain.model.SalaDeReuniao;
import java.math.BigDecimal;

public record SalaDeReuniaoResponseDTO(
        Long id,
        String nome,
        Integer andar,
        boolean possuiProjetor,
        Integer capacidadeMaxima,
        BigDecimal valorPorHora,
        boolean ativa) {

    public static SalaDeReuniaoResponseDTO deEntidade(SalaDeReuniao sala) {
        return new SalaDeReuniaoResponseDTO(
                sala.getId(),
                sala.getNome(),
                sala.getAndar(),
                sala.isPossuiProjetor(),
                sala.getCapacidade() != null ? sala.getCapacidade().getMaximoPessoas() : null,
                sala.getTarifa() != null ? sala.getTarifa().getValorPorHora() : null,
                sala.isAtiva());
    }
}
