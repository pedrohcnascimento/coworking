package com.phcnforge.coworking.service;

import com.phcnforge.coworking.domain.model.SalaDeReuniao;
import com.phcnforge.coworking.domain.vo.Capacidade;
import com.phcnforge.coworking.domain.vo.Tarifa;
import com.phcnforge.coworking.dto.request.SalaDeReuniaoRequestDTO;
import com.phcnforge.coworking.dto.response.SalaDeReuniaoResponseDTO;
import com.phcnforge.coworking.exception.ConflitoReservaException;
import com.phcnforge.coworking.exception.RecursoNaoEncontradoException;
import com.phcnforge.coworking.repository.SalaDeReuniaoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalaDeReuniaoService {

    private final SalaDeReuniaoRepository salaRepository;

    @Transactional
    public SalaDeReuniaoResponseDTO criar(SalaDeReuniaoRequestDTO dto) {
        if (salaRepository.existsByNomeIgnoreCase(dto.nome().trim())) {
            throw new ConflitoReservaException("Já existe uma sala de reunião com o nome: " + dto.nome());
        }

        SalaDeReuniao sala = SalaDeReuniao.builder()
                .nome(dto.nome().trim())
                .andar(dto.andar())
                .possuiProjetor(dto.possuiProjetor() != null ? dto.possuiProjetor() : false)
                .capacidade(new Capacidade(dto.capacidadeMaxima()))
                .tarifa(new Tarifa(dto.valorPorHora()))
                .ativa(dto.ativa() != null ? dto.ativa() : true)
                .build();

        return SalaDeReuniaoResponseDTO.deEntidade(salaRepository.save(sala));
    }

    @Transactional(readOnly = true)
    public List<SalaDeReuniaoResponseDTO> listarTodas() {
        return salaRepository.findAll().stream()
                .map(SalaDeReuniaoResponseDTO::deEntidade)
                .toList();
    }

    @Transactional(readOnly = true)
    public SalaDeReuniaoResponseDTO buscarPorId(Long id) {
        SalaDeReuniao sala = salaRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala de reunião não encontrada com ID: " + id));
        return SalaDeReuniaoResponseDTO.deEntidade(sala);
    }

    @Transactional
    public void deletar(Long id) {
        SalaDeReuniao sala = salaRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala de reunião não encontrada com ID: " + id));
        salaRepository.delete(sala);
    }
}
