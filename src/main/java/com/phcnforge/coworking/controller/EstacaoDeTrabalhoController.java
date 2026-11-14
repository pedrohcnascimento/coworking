package com.phcnforge.coworking.controller;

import com.phcnforge.coworking.dto.request.EstacaoDeTrabalhoRequestDTO;
import com.phcnforge.coworking.dto.response.EstacaoDeTrabalhoResponseDTO;
import com.phcnforge.coworking.service.EstacaoDeTrabalhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estacoes")
@RequiredArgsConstructor
@Tag(name = "Estações de Trabalho", description = "Gerenciamento de mesas e estações disponíveis para reserva")
public class EstacaoDeTrabalhoController {

    private final EstacaoDeTrabalhoService estacaoService;

    @PostMapping
    @Operation(summary = "Cadastrar estação de trabalho")
    public ResponseEntity<EstacaoDeTrabalhoResponseDTO> criar(
            @Valid @RequestBody EstacaoDeTrabalhoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estacaoService.criar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todas as estações de trabalho")
    public ResponseEntity<List<EstacaoDeTrabalhoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(estacaoService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar estação de trabalho por ID")
    public ResponseEntity<EstacaoDeTrabalhoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(estacaoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados de uma estação de trabalho")
    public ResponseEntity<EstacaoDeTrabalhoResponseDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody EstacaoDeTrabalhoRequestDTO dto) {
        return ResponseEntity.ok(estacaoService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/disponibilidade")
    @Operation(summary = "Ativar ou inativar estação de trabalho")
    public ResponseEntity<EstacaoDeTrabalhoResponseDTO> alterarDisponibilidade(
            @PathVariable Long id, @RequestParam boolean ativa) {
        return ResponseEntity.ok(estacaoService.atualizarDisponibilidade(id, ativa));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover estação de trabalho")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        estacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
