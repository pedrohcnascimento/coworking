package com.phcnforge.coworking.controller;

import com.phcnforge.coworking.dto.request.SalaDeReuniaoRequestDTO;
import com.phcnforge.coworking.dto.response.SalaDeReuniaoResponseDTO;
import com.phcnforge.coworking.service.SalaDeReuniaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/salas-reuniao")
@RequiredArgsConstructor
@Tag(name = "Salas de Reunião", description = "Cadastro e consulta de salas de reunião corporativas")
public class SalaDeReuniaoController {

    private final SalaDeReuniaoService salaService;

    @PostMapping
    @Operation(summary = "Cadastrar sala de reunião")
    public ResponseEntity<SalaDeReuniaoResponseDTO> criar(@Valid @RequestBody SalaDeReuniaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salaService.criar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar salas de reunião")
    public ResponseEntity<List<SalaDeReuniaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(salaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sala de reunião por ID")
    public ResponseEntity<SalaDeReuniaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(salaService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover sala de reunião")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        salaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
