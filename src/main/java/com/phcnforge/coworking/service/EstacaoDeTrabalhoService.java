package com.phcnforge.coworking.service;

import com.phcnforge.coworking.domain.model.EstacaoDeTrabalho;
import com.phcnforge.coworking.domain.vo.Capacidade;
import com.phcnforge.coworking.domain.vo.Tarifa;
import com.phcnforge.coworking.dto.request.EstacaoDeTrabalhoRequestDTO;
import com.phcnforge.coworking.dto.response.EstacaoDeTrabalhoResponseDTO;
import com.phcnforge.coworking.exception.ConflitoReservaException;
import com.phcnforge.coworking.exception.RecursoNaoEncontradoException;
import com.phcnforge.coworking.repository.EstacaoDeTrabalhoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstacaoDeTrabalhoService {

    private final EstacaoDeTrabalhoRepository estacaoRepository;

    @Transactional
    public EstacaoDeTrabalhoResponseDTO criar(EstacaoDeTrabalhoRequestDTO dto) {
        String codigoNormalizado = dto.codigo().trim().toUpperCase();
        if (estacaoRepository.existsByCodigo(codigoNormalizado)) {
            throw new ConflitoReservaException("Já existe uma estação de trabalho com o código: " + codigoNormalizado);
        }

        EstacaoDeTrabalho estacao = EstacaoDeTrabalho.builder()
                .codigo(codigoNormalizado)
                .descricao(dto.descricao().trim())
                .capacidade(new Capacidade(dto.capacidadeMaxima()))
                .tarifa(new Tarifa(dto.valorPorHora()))
                .ativa(dto.ativa() != null ? dto.ativa() : true)
                .build();

        return EstacaoDeTrabalhoResponseDTO.deEntidade(estacaoRepository.save(estacao));
    }

    @Transactional(readOnly = true)
    public List<EstacaoDeTrabalhoResponseDTO> listarTodas() {
        return estacaoRepository.findAll().stream()
                .map(EstacaoDeTrabalhoResponseDTO::deEntidade)
                .toList();
    }

    @Transactional(readOnly = true)
    public EstacaoDeTrabalhoResponseDTO buscarPorId(Long id) {
        return EstacaoDeTrabalhoResponseDTO.deEntidade(buscarEntidadePorId(id));
    }

    @Transactional
    public EstacaoDeTrabalhoResponseDTO atualizar(Long id, EstacaoDeTrabalhoRequestDTO dto) {
        EstacaoDeTrabalho estacao = buscarEntidadePorId(id);
        String codigoNormalizado = dto.codigo().trim().toUpperCase();

        if (!estacao.getCodigo().equalsIgnoreCase(codigoNormalizado)
                && estacaoRepository.existsByCodigo(codigoNormalizado)) {
            throw new ConflitoReservaException("Já existe outra estação com o código: " + codigoNormalizado);
        }

        estacao.setCodigo(codigoNormalizado);
        estacao.setDescricao(dto.descricao().trim());
        estacao.setCapacidade(new Capacidade(dto.capacidadeMaxima()));
        estacao.setTarifa(new Tarifa(dto.valorPorHora()));
        estacao.setAtiva(dto.ativa() != null ? dto.ativa() : true);

        return EstacaoDeTrabalhoResponseDTO.deEntidade(estacaoRepository.save(estacao));
    }

    @Transactional
    public EstacaoDeTrabalhoResponseDTO atualizarDisponibilidade(Long id, boolean ativa) {
        EstacaoDeTrabalho estacao = buscarEntidadePorId(id);
        estacao.setAtiva(ativa);
        return EstacaoDeTrabalhoResponseDTO.deEntidade(estacaoRepository.save(estacao));
    }

    @Transactional
    public void deletar(Long id) {
        EstacaoDeTrabalho estacao = buscarEntidadePorId(id);
        estacaoRepository.delete(estacao);
    }

    @Transactional(readOnly = true)
    public EstacaoDeTrabalho buscarEntidadePorId(Long id) {
        return estacaoRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estação de trabalho não encontrada com ID: " + id));
    }
}
